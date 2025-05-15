package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseService;
import br.com.alura.AluraFake.task.dto.OpenTextTaskRequestDTO;
import br.com.alura.AluraFake.util.error.ResourceIllegalStateException;
import br.com.alura.AluraFake.util.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TaskService {
    private final CourseService courseService;
    private final TaskRepository taskRepository;

    public String createOpenTextTask(OpenTextTaskRequestDTO openTextTaskRequestDTO){
        Long courseIdFromOpenTextTask = openTextTaskRequestDTO.courseId();
        Course course = courseService.findCourseById(courseIdFromOpenTextTask);

        if (!"BUILDING".equals(course.getStatus())) {
            throw new ResourceIllegalStateException("Curso não está em BUILDING");
        }

        

    }

    public  OpenTextTask findTaskById(Long courseId){
        Optional<Course> obj = courseRepository.findById(courseId);
        if(obj.isEmpty()) throw new ResourceNotFoundException("Curso não encontrado");
        return obj.get();
    }


}
