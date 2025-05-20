package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.TaskRepository;
import br.com.alura.AluraFake.task.TaskService;
import br.com.alura.AluraFake.task.Type;
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
    private  final TaskRepository taskRepository;

    public Course findCourseById(Long courseId){
        Optional<Course> obj = courseRepository.findById(courseId);
        if(obj.isEmpty()) throw new ResourceNotFoundException("Curso não encontrado");
        return obj.get();
    }

    public void publishCourse(Long id) {
        Course course = findCourseById(id);

        if (course.getStatus() != Status.BUILDING) {
            throw new ResourceIllegalStateException("Curso já publicado.");
        }

        List<Task> tasks = taskRepository.findByCourseOrderByOrder(course);

        if (tasks.isEmpty()) {
            throw new ResourceIllegalStateException("Curso precisa de tarefas para ser publicado.");
        }

        boolean hasOpenText = tasks.stream().anyMatch(t -> t.getType() == Type.OPEN_TEXT);
        boolean hasSingleChoice = tasks.stream().anyMatch(t -> t.getType() == Type.SINGLE_CHOICE);
        boolean hasMultipleChoice = tasks.stream().anyMatch(t -> t.getType() == Type.MULTIPLE_CHOICE);

        if (!(hasOpenText && hasSingleChoice && hasMultipleChoice)) {
            throw new ResourceIllegalStateException("Curso precisa de todos os tipos de tarefas.");
        }

        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getOrder() != i + 1) {
                throw new ResourceIllegalStateException("Tarefas devem estar em ordem sequencial a partir de 1.");
            }
        }

        course.setStatus(Status.PUBLISHED);
        course.setPublishedAt(LocalDateTime.now());
        courseRepository.save(course);
    }
}
