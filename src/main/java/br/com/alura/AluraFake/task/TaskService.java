package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseService;
import br.com.alura.AluraFake.task.dto.OpenTextTaskRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class TaskService {
    private final CourseService courseService;
    private final TaskRepository taskRepository;

    public String createOpenTextTask(OpenTextTaskRequestDTO openTextTaskRequestDTO){
        Long courseIdFromOpenTextTask = openTextTaskRequestDTO.courseId();
        Course courseIdFromCourse = courseService.findCourseById(courseIdFromOpenTextTask);



    }


}
