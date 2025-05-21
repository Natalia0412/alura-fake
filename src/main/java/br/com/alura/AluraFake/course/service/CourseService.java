package br.com.alura.AluraFake.course.service;

import br.com.alura.AluraFake.course.dto.CoursePublishedDTO;
import br.com.alura.AluraFake.course.mapper.CourseMapper;
import br.com.alura.AluraFake.course.repository.CourseRepository;
import br.com.alura.AluraFake.course.model.Status;
import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.task.model.Task;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import br.com.alura.AluraFake.task.model.Type;
import br.com.alura.AluraFake.task.service.TaskService;
import br.com.alura.AluraFake.util.error.ResourceIllegalStateException;
import br.com.alura.AluraFake.util.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private  final TaskService taskService;
    private final CourseMapper courseMapper;

    public Course findCourseById(Long courseId){
        return courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado"));
    }

    public CoursePublishedDTO publishCourse(Long id) {
        Course course = findCourseById(id);

        if (course.getStatus() != Status.BUILDING) {
            throw new ResourceIllegalStateException("Curso já publicado.");
        }

        List<Task> tasks  = taskService.checkCourseHasTask(course);

        taskService.checkCourseHasOneOfEachTypeOfTask(tasks);

        course.setStatus(Status.PUBLISHED);
        course.setPublishedAt(LocalDateTime.now());
        Course courseSaved = courseRepository.save(course);

        return courseMapper.toCoursePublishedDTO(courseSaved);


    }
}
