package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseService;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.task.dto.OpenTextTaskDTO;
import br.com.alura.AluraFake.task.dto.OptionDTO;
import br.com.alura.AluraFake.task.dto.SingleChoiceTaskDTO;
import br.com.alura.AluraFake.util.error.ResourceIllegalArgumentException;
import br.com.alura.AluraFake.util.error.ResourceIllegalStateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TaskService {
    private final CourseService courseService;
    private final TaskRepository taskRepository;
    private final TaskMapper mapper;

    public OpenTextTaskDTO createOpenTextTask(OpenTextTaskDTO dto) {

        Course course = validateCourseAndOrder(dto.courseId(), dto.order());

        validateStatement(dto.statement(), course);

        reorderIfNecessary(course, dto.order());

        Task task = mapper.toTask(dto);
        task.setCourse(course);
        task.setType(Type.OPEN_TEXT);
        taskRepository.save(task);

        return mapper.toDto(task);

    }

    public SingleChoiceTaskDTO createSingleChoiceTask(SingleChoiceTaskDTO dto) {

        Course course = validateCourseAndOrder(dto.courseId(), dto.order());

        validateStatement(dto.statement(), course);

       Long isCorrectCount =  dto.options().stream().filter(OptionDTO::isCorrect).count();

       if(isCorrectCount != 1){
           throw new ResourceIllegalArgumentException("Deve haver exatamente uma opção correta.");
       }

        reorderIfNecessary(course, dto.order());

       
    }

    private Course validateCourseAndOrder(Long courseId, Integer order) {
        Course course = courseService.findCourseById(courseId);
        if (course.getStatus() != Status.BUILDING) {
            throw new ResourceIllegalStateException("Curso precisa estar com status BUILDING");
        }

        // Verifica continuidade da ordem
        List<Task> tasks = taskRepository.findByCourseOrderByOrder(course);
        if (order > tasks.size() + 1) {
            throw new ResourceIllegalArgumentException("Ordem inválida: não pode haver saltos na sequência.");
        }

        return course;
    }

    private void validateStatement(String statement, Course course) {
        boolean exists = taskRepository.existsByCourseAndStatement(course, statement);
        if (exists) {
            throw new ResourceIllegalArgumentException("Já existe uma atividade com esse enunciado para o curso.");
        }
    }

    private void reorderIfNecessary(Course course, Integer newOrder) {
        List<Task> tasks = taskRepository.findByCourseOrderByOrder(course);
        for (Task t : tasks) {
            if (t.getOrder() >= newOrder) {
                t.setOrder(t.getOrder() + 1);
            }
        }
    }


}
