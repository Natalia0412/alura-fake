package br.com.alura.AluraFake.course;
import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.TaskRepository;
import br.com.alura.AluraFake.task.Type;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.util.error.ResourceIllegalStateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {
    @InjectMocks
    private CourseService courseService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private TaskRepository taskRepository;

    private Course buildCourse(Status status) {
        User instructor = mock(User.class);
        when(instructor.isInstructor()).thenReturn(true);
        Course course = new Course("Java", "desc", instructor);
        course.setStatus(status);
        return course;
    }

    private void mockCourseLookup(Long courseId, Course course) {
        when(courseService.findCourseById(courseId)).thenReturn(course);
        when(taskRepository.findByCourseOrderByOrder(course)).thenReturn(Collections.emptyList());
    }
    @Test
    void shouldPublishCourseSuccessfully() {
        Course course = buildCourse(Status.BUILDING);


        List<Task> tasks = Arrays.asList(
                Task.builder().order(1).type(Type.OPEN_TEXT).build(),
                Task.builder().order(2).type(Type.SINGLE_CHOICE).build(),
                Task.builder().order(3).type(Type.MULTIPLE_CHOICE).build()
        );

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.findByCourseOrderByOrder(course)).thenReturn(tasks);

        courseService.publishCourse(1L);

        verify(courseRepository).save(course);
        assertEquals(Status.PUBLISHED, course.getStatus());
        assertNotNull(course.getPublishedAt(), "Data de publicação deve ser registrada");
    }

    @Test
    void shouldThrowException_whenCourseAlreadyPublished() {
        Course course = buildCourse(Status.PUBLISHED);

        when(courseRepository.findById(1L)).thenReturn(java.util.Optional.of(course));

        assertThatThrownBy(() -> courseService.publishCourse(1L))
                .isInstanceOf(ResourceIllegalStateException.class)
                .hasMessageContaining("Curso já publicado");
    }


    @Test
    void shouldThrowException_whenMissingOneTaskType() {
        Course course = buildCourse(Status.BUILDING);

        List<Task> tasks = Arrays.asList(
                Task.builder().order(1).type(Type.OPEN_TEXT).build(),
                Task.builder().order(2).type(Type.SINGLE_CHOICE).build()
        );

        when(courseRepository.findById(1L)).thenReturn(java.util.Optional.of(course));
        when(taskRepository.findByCourseOrderByOrder(course)).thenReturn(tasks);

        assertThatThrownBy(() -> courseService.publishCourse(1L))
                .isInstanceOf(ResourceIllegalStateException.class)
                .hasMessageContaining("Curso precisa de todos os tipos de tarefas.");
    }

    @Test
    void shouldThrowException_whenTasksNotSequential() {
        Course course = buildCourse(Status.BUILDING);

        List<Task> tasks = Arrays.asList(
                Task.builder().order(1).type(Type.OPEN_TEXT).build(),
                Task.builder().order(3).type(Type.SINGLE_CHOICE).build(), // falta ordem 2
                Task.builder().order(4).type(Type.MULTIPLE_CHOICE).build()
        );

        when(courseRepository.findById(1L)).thenReturn(java.util.Optional.of(course));
        when(taskRepository.findByCourseOrderByOrder(course)).thenReturn(tasks);

        assertThatThrownBy(() -> courseService.publishCourse(1L))
                .isInstanceOf(ResourceIllegalStateException.class)
                .hasMessageContaining("Tarefas devem estar em ordem sequencial a partir de 1.");
    }


    @Test
    void shouldThrowException_whenMissingTasks() {
        Course course = buildCourse(Status.BUILDING);

        when(courseRepository.findById(1L)).thenReturn(java.util.Optional.of(course));
        when(taskRepository.findByCourseOrderByOrder(course)).thenReturn(List.of());

        assertThatThrownBy(() -> courseService.publishCourse(1L))
                .isInstanceOf(ResourceIllegalStateException.class)
                .hasMessageContaining("Curso precisa de tarefas para ser publicado.");
    }


}
