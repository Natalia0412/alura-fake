package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseService;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.task.dto.OpenTextTaskDTO;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.util.error.ResourceIllegalArgumentException;
import br.com.alura.AluraFake.util.error.ResourceIllegalStateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test
    void shouldInsertTaskAtTheEndSuccessfully() {
        User instructor = mock(User.class);
        when(instructor.isInstructor()).thenReturn(true);

        Course course = new Course("Java", "Descrição", instructor);
        course.setStatus(Status.BUILDING);

        OpenTextTaskDTO dto = new OpenTextTaskDTO(1L, "O que é Java?", 1);

        Task taskEntity = Task.builder()
                .statement(dto.statement())
                .order(dto.order())
                .course(course)
                .type(Type.OPEN_TEXT)
                .build();

        OpenTextTaskDTO expectedDto = new OpenTextTaskDTO(1L, "O que é Java?", 1);

        // Act
        when(courseService.findCourseById(dto.courseId())).thenReturn(course);
        when(taskRepository.findByCourseOrderByOrder(course)).thenReturn(Collections.emptyList());
        when(taskMapper.toTask(dto)).thenReturn(taskEntity);
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);
        when(taskMapper.toDto(taskEntity)).thenReturn(expectedDto);

        OpenTextTaskDTO result = taskService.createOpenTextTask(dto);

        // Assert
        assertEquals(expectedDto.statement(), result.statement());
        assertEquals(expectedDto.order(), result.order());

        verify(taskRepository).save(taskEntity);
        verify(taskMapper).toTask(dto);
        verify(taskMapper).toDto(taskEntity);
    }

    @Test
    void shouldThrowException_whenStatementIsDuplicatedInSameCourse() {
        // Arrange
        Long courseId = 1L;
        String duplicatedStatement = "Explique o que é uma classe em Java.";

        User instructor = mock(User.class);
        when(instructor.isInstructor()).thenReturn(true);

        Course course = new Course("Java", "descrição", instructor);
        course.setStatus(Status.BUILDING);

        Task existingTask = Task.builder()
                .statement(duplicatedStatement)
                .order(1)
                .course(course)
                .type(Type.OPEN_TEXT)
                .build();

        OpenTextTaskDTO dto = new OpenTextTaskDTO(courseId, duplicatedStatement, 2);

        when(courseService.findCourseById(dto.courseId())).thenReturn(course);
        when(taskRepository.findByCourseOrderByOrder(course)).thenReturn(List.of(existingTask));
        when(taskRepository.existsByCourseAndStatement(course, duplicatedStatement)).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> taskService.createOpenTextTask(dto))
                .isInstanceOf(ResourceIllegalArgumentException.class)
                .hasMessageContaining("Já existe uma atividade com esse enunciado para o curso.");
    }


    @Test
    void shouldThrowException_whenOrderSkipsSequence() {
        User instructor = mock(User.class);
        when(instructor.isInstructor()).thenReturn(true);
        Long courseId = 1L;
        Course course = new Course("Java", "desc", instructor);
        course.setStatus(Status.BUILDING);

        // Suponha que já exista 1 tarefa na ordem 1
        Task existingTask = Task.builder()
                .id(10L)
                .statement("Tarefa existente")
                .order(1)
                .course(course)
                .type(Type.OPEN_TEXT)
                .build();

        when(courseService.findCourseById(courseId)).thenReturn(course);
        when(taskRepository.findByCourseOrderByOrder(course)).thenReturn(List.of(existingTask));

        // Tenta inserir direto na ordem 3 (pulo do 2)
        OpenTextTaskDTO dto = new OpenTextTaskDTO(courseId, "O que é polimorfismo?", 3);

        assertThatThrownBy(() -> taskService.createOpenTextTask(dto))
                .isInstanceOf(ResourceIllegalArgumentException.class)
                .hasMessageContaining("Ordem inválida: não pode haver saltos na sequência.");
    }

    @Test
    void shouldThrowException_whenStatusNotBuilding() {
        User instructor = mock(User.class);
        when(instructor.isInstructor()).thenReturn(true);
        Long courseId = 1L;
        Course course = new Course("Java", "desc", instructor);
        course.setStatus(Status.PUBLISHED);

        OpenTextTaskDTO dto = new OpenTextTaskDTO(courseId, "O que é polimorfismo?", 1);
        when(courseService.findCourseById(dto.courseId())).thenReturn(course);


        assertThatThrownBy(() -> taskService.createOpenTextTask(dto))
                .isInstanceOf(ResourceIllegalStateException.class)
                .hasMessageContaining("Curso precisa estar com status BUILDING");

    }
}
