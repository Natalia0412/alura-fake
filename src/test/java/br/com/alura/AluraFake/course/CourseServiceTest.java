package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.course.dto.CoursePublishedDTO;
import br.com.alura.AluraFake.course.mapper.CourseMapper;
import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.model.Status;
import br.com.alura.AluraFake.course.repository.CourseRepository;
import br.com.alura.AluraFake.course.service.CourseService;
import br.com.alura.AluraFake.task.model.Task;
import br.com.alura.AluraFake.task.model.Type;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import br.com.alura.AluraFake.task.service.TaskService;
import br.com.alura.AluraFake.user.model.User;
import br.com.alura.AluraFake.util.error.ResourceIllegalStateException;
import br.com.alura.AluraFake.util.error.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {
    @InjectMocks
    private CourseService courseService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskService taskService;

    @Mock
    private CourseMapper courseMapper;

    private Course buildCourse(Status status) {
        User instructor = mock(User.class);
        when(instructor.isInstructor()).thenReturn(true);
        Long courseId = 1L;
        Course course = new Course("Java", "desc", instructor);
        setField(course, "id", courseId);
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
        when(taskService.checkCourseHasTask(course)).thenReturn(tasks);
        doNothing().when(taskService).checkCourseHasOneOfEachTypeOfTask(tasks);
        when(courseRepository.save(course)).thenReturn(course);
        when(courseMapper.toCoursePublishedDTO(course)).thenReturn(mock(CoursePublishedDTO.class));

        CoursePublishedDTO dto = courseService.publishCourse(1L);


        verify(courseRepository).save(course);
        assertEquals(Status.PUBLISHED, course.getStatus());
        assertNotNull(course.getPublishedAt());
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
        when(taskService.checkCourseHasTask(course)).thenReturn(tasks);
        doThrow(new ResourceIllegalStateException("Curso precisa de todos os tipos de tarefas."))
                .when(taskService).checkCourseHasOneOfEachTypeOfTask(tasks);

        assertThatThrownBy(() -> courseService.publishCourse(1L))
                .isInstanceOf(ResourceIllegalStateException.class)
                .hasMessageContaining("Curso precisa de todos os tipos de tarefas.");
    }


    @Test
    void shouldThrowException_whenMissingTasks() {
        Course course = buildCourse(Status.BUILDING);

        when(courseRepository.findById(1L)).thenReturn(java.util.Optional.of(course));
        when(taskService.checkCourseHasTask(course)).thenThrow(new ResourceIllegalStateException("Curso precisa de tarefas para ser publicado."));

        assertThatThrownBy(() -> courseService.publishCourse(1L))
                .isInstanceOf(ResourceIllegalStateException.class)
                .hasMessageContaining("Curso precisa de tarefas para ser publicado.");
    }

    @Test
    void shouldThrowException_whenCourseNotFound() {
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.publishCourse(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Curso não encontrado");
    }


}
