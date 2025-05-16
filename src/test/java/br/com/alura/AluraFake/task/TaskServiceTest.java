package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseService;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.task.dto.OpenTextTaskDTO;
import br.com.alura.AluraFake.task.dto.OptionDTO;
import br.com.alura.AluraFake.task.dto.SingleChoiceTaskDTO;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.util.error.ResourceIllegalArgumentException;
import br.com.alura.AluraFake.util.error.ResourceIllegalStateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @InjectMocks
    private TaskService taskService;

    @Mock
    private CourseService courseService;

    @Mock
    private TaskRepository taskRepository;


    @Mock
    private TaskMapper taskMapper;


    private Course buildCourse(Status status) {
        User instructor = mock(User.class);
        when(instructor.isInstructor()).thenReturn(true);
        Course course = new Course("Java", "desc", instructor);
        course.setStatus(status);
        return course;
    }

    private List<OptionDTO> options(String... texts) {
        return Arrays.stream(texts)
                .map(text -> new OptionDTO(text, false))
                .toList();
    }

    private List<OptionDTO> optionsWithCorrect(String correct, String... incorrect) {
        List<OptionDTO> list = new ArrayList<>();
        list.add(new OptionDTO(correct, true));
        for (String i : incorrect) {
            list.add(new OptionDTO(i, false));
        }
        return list;
    }

    private void mockCourseLookup(Long courseId, Course course) {
        when(courseService.findCourseById(courseId)).thenReturn(course);
        when(taskRepository.findByCourseOrderByOrder(course)).thenReturn(Collections.emptyList());
    }

    @Test
    void shouldInsertTaskAtTheEndSuccessfully() {
        Course course = buildCourse(Status.BUILDING);
        OpenTextTaskDTO dto = new OpenTextTaskDTO(1L, "O que é Java?", 1);
        Task taskEntity = Task.builder()
                .statement(dto.statement())
                .order(dto.order())
                .course(course)
                .type(Type.OPEN_TEXT)
                .build();
        OpenTextTaskDTO expectedDto = new OpenTextTaskDTO(1L, "O que é Java?", 1);

        mockCourseLookup(dto.courseId(), course);
        when(taskMapper.toTask(dto)).thenReturn(taskEntity);
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);
        when(taskMapper.toDto(taskEntity)).thenReturn(expectedDto);

        OpenTextTaskDTO result = taskService.createOpenTextTask(dto);

        assertEquals(expectedDto.statement(), result.statement());
        assertEquals(expectedDto.order(), result.order());

        verify(taskRepository).save(taskEntity);
        verify(taskMapper).toTask(dto);
        verify(taskMapper).toDto(taskEntity);
    }

    @Test
    void shouldThrowException_whenStatementIsDuplicatedInSameCourse() {
        Course course = buildCourse(Status.BUILDING);
        String duplicatedStatement = "Explique o que é uma classe em Java.";
        OpenTextTaskDTO dto = new OpenTextTaskDTO(1L, duplicatedStatement, 1);

        mockCourseLookup(dto.courseId(), course);
        when(taskRepository.existsByCourseAndStatement(course, duplicatedStatement)).thenReturn(true);

        assertThatThrownBy(() -> taskService.createOpenTextTask(dto))
                .isInstanceOf(ResourceIllegalArgumentException.class)
                .hasMessageContaining("Já existe uma atividade com esse enunciado para o curso.");
    }

    @Test
    void shouldThrowException_whenOrderSkipsSequence() {
        Course course = buildCourse(Status.BUILDING);
        Task existingTask = Task.builder().id(10L).statement("Tarefa existente").order(1).course(course).type(Type.OPEN_TEXT).build();
        when(courseService.findCourseById(1L)).thenReturn(course);
        when(taskRepository.findByCourseOrderByOrder(course)).thenReturn(List.of(existingTask));

        OpenTextTaskDTO dto = new OpenTextTaskDTO(1L, "O que é polimorfismo?", 3);

        assertThatThrownBy(() -> taskService.createOpenTextTask(dto))
                .isInstanceOf(ResourceIllegalArgumentException.class)
                .hasMessageContaining("Ordem inválida: não pode haver saltos na sequência.");
    }

    @Test
    void shouldThrowException_whenStatusNotBuilding() {
        Course course = buildCourse(Status.PUBLISHED);
        OpenTextTaskDTO dto = new OpenTextTaskDTO(1L, "O que é polimorfismo?", 1);
        when(courseService.findCourseById(dto.courseId())).thenReturn(course);

        assertThatThrownBy(() -> taskService.createOpenTextTask(dto))
                .isInstanceOf(ResourceIllegalStateException.class)
                .hasMessageContaining("Curso precisa estar com status BUILDING");
    }

    @Test
    void shouldThrowException_whenMoreThanOneCorrectOption() {
        Course course = buildCourse(Status.BUILDING);
        List<OptionDTO> options = Arrays.asList(
                new OptionDTO("Java", true),
                new OptionDTO("Python", true)
        );
        SingleChoiceTaskDTO dto = new SingleChoiceTaskDTO(1L, "Qual linguagem?", 1, options);

        mockCourseLookup(dto.courseId(), course);
        when(taskRepository.existsByCourseAndStatement(course, dto.statement())).thenReturn(false);

        assertThatThrownBy(() -> taskService.createSingleChoiceTask(dto))
                .isInstanceOf(ResourceIllegalArgumentException.class)
                .hasMessageContaining("exatamente uma opção correta");
    }

    @Test
    void shouldThrowException_whenOptionsAreDuplicated() {
        Course course = buildCourse(Status.BUILDING);
        List<OptionDTO> options = Arrays.asList(
                new OptionDTO("Java", true),
                new OptionDTO("Java", false)
        );
        SingleChoiceTaskDTO dto = new SingleChoiceTaskDTO(1L, "Qual linguagem?", 1, options);

        mockCourseLookup(dto.courseId(), course);
        when(taskRepository.existsByCourseAndStatement(course, dto.statement())).thenReturn(false);

        assertThatThrownBy(() -> taskService.createSingleChoiceTask(dto))
                .isInstanceOf(ResourceIllegalArgumentException.class)
                .hasMessageContaining("As alternativas não podem ser repetidas.");
    }

    @Test
    void shouldThrowException_whenOptionEqualsStatement() {
        Course course = buildCourse(Status.BUILDING);
        List<OptionDTO> options = optionsWithCorrect("Qual linguagem?", "Python");
        SingleChoiceTaskDTO dto = new SingleChoiceTaskDTO(1L, "Qual linguagem?", 1, options);

        mockCourseLookup(dto.courseId(), course);
        when(taskRepository.existsByCourseAndStatement(course, dto.statement())).thenReturn(false);

        assertThatThrownBy(() -> taskService.createSingleChoiceTask(dto))
                .isInstanceOf(ResourceIllegalArgumentException.class)
                .hasMessageContaining("Nenhuma alternativa pode ser igual ao enunciado da atividade.");
    }

    @Test
    void shouldThrowException_whenCourseNotBuilding() {
        Course course = buildCourse(Status.PUBLISHED);
        List<OptionDTO> options = optionsWithCorrect("Java", "Python");
        SingleChoiceTaskDTO dto = new SingleChoiceTaskDTO(1L, "Qual linguagem?", 1, options);

        when(courseService.findCourseById(dto.courseId())).thenReturn(course);

        assertThatThrownBy(() -> taskService.createSingleChoiceTask(dto))
                .isInstanceOf(ResourceIllegalStateException.class)
                .hasMessageContaining("status BUILDING");
    }

    @Test
    void shouldCreateSingleChoiceTaskSuccessfully() {
        Course course = buildCourse(Status.BUILDING);
        Long courseId = 1L;
        String statement = "Qual dessas é uma linguagem de programação?";
        int order = 1;

        List<OptionDTO> options = Arrays.asList(
                new OptionDTO("Java", true),
                new OptionDTO("Banana", false),
                new OptionDTO("Maça", false)
        );

        SingleChoiceTaskDTO dto = new SingleChoiceTaskDTO(courseId, statement, order, options);

        Task mappedTask = Task.builder()
                .statement(statement)
                .order(order)
                .type(Type.SINGLE_CHOICE)
                .course(course)
                .build();

        when(courseService.findCourseById(courseId)).thenReturn(course);
        when(taskRepository.findByCourseOrderByOrder(course)).thenReturn(Collections.emptyList());
        when(taskRepository.existsByCourseAndStatement(course, statement)).thenReturn(false);
        when(taskMapper.singleDtoToTask(dto)).thenReturn(mappedTask);
        when(taskRepository.save(any(Task.class))).thenReturn(mappedTask);
        when(taskMapper.toSingDto(mappedTask)).thenReturn(dto);

        // Act
        SingleChoiceTaskDTO result = taskService.createSingleChoiceTask(dto);

        // Assert
        assertEquals(dto.statement(), result.statement());
        assertEquals(dto.order(), result.order());
        assertEquals(dto.options().size(), result.options().size());

        verify(courseService).findCourseById(courseId);
        verify(taskRepository, times(2)).findByCourseOrderByOrder(course); // corrigido aqui
        verify(taskRepository).save(any(Task.class));
        verify(taskMapper).singleDtoToTask(dto);
        verify(taskMapper).toSingDto(mappedTask);
    }

}
