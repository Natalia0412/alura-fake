package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseService;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.task.dto.OpenTextTaskDTO;

import br.com.alura.AluraFake.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

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
}
