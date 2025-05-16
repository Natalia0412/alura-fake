package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseService;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.task.dto.OpenTextTaskRequestDTO;
import br.com.alura.AluraFake.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest

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

        Course course = new Course("Java Basics", "Intro course", instructor);
        course.setStatus(Status.BUILDING);

        OpenTextTaskRequestDTO dto = new OpenTextTaskRequestDTO(1L, "What is Java?", 1);
        OpenTextTask mappedTask = new OpenTextTask();
        mappedTask.setStatement(dto.statement());
        mappedTask.setOrder(dto.order());
        mappedTask.setCourse(course);

        when(courseService.findCourseById(dto.courseId())).thenReturn(course);
        when(taskRepository.countByCourse(course)).thenReturn(0);
        when(taskMapper.toEntity(dto)).thenReturn(mappedTask);
        when(taskRepository.save(mappedTask)).thenReturn(mappedTask);

        OpenTextTask result = taskService.createOpenTextTask(dto);



        assertEquals("What is Java?", result.getStatement());
        assertEquals(1, result.getOrder());
        verify(taskRepository).save(mappedTask);
    }
}
