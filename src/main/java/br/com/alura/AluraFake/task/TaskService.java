package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseService;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.task.dto.MultipleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.OpenTextTaskDTO;
import br.com.alura.AluraFake.task.dto.OptionDTO;
import br.com.alura.AluraFake.task.dto.SingleChoiceTaskDTO;
import br.com.alura.AluraFake.util.error.ResourceIllegalArgumentException;
import br.com.alura.AluraFake.util.error.ResourceIllegalStateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
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
        validateOptions(dto.statement(), dto.options(), false);
        reorderIfNecessary(course, dto.order());

        Task task = mapper.singleDtoToTask(dto);
        task.setCourse(course);
        task.setType(Type.SINGLE_CHOICE);
        task.setOptions(mapOptions(dto.options(), task));
        taskRepository.save(task);

        return mapper.toSingDto(task);
    }

    public MultipleChoiceTaskDTO createMultipleChoiceTask(MultipleChoiceTaskDTO dto) {
        Course course = validateCourseAndOrder(dto.courseId(), dto.order());
        validateStatement(dto.statement(), course);
        validateOptions(dto.statement(), dto.options(), true);
        reorderIfNecessary(course, dto.order());

        Task task = mapper.multipleChoiseDtoToTask(dto);
        task.setCourse(course);
        task.setType(Type.MULTIPLE_CHOICE);
        task.setOptions(mapOptions(dto.options(), task));
        taskRepository.save(task);

        return mapper.taskToMultiplechoiceDto(task);

    }

    private Course validateCourseAndOrder(Long courseId, Integer order) {
        Course course = courseService.findCourseById(courseId);
        if (course.getStatus() != Status.BUILDING) {
            throw new ResourceIllegalStateException("Curso precisa estar com status BUILDING");
        }

        List<Task> tasks = taskRepository.findByCourseOrderByOrder(course);
        if (order > tasks.size() + 1) {
            throw new ResourceIllegalArgumentException("Ordem inválida: não pode haver saltos na sequência.");
        }

        return course;
    }

    private void validateStatement(String statement, Course course) {
        if (taskRepository.existsByCourseAndStatement(course, statement)) {
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

    private void validateOptions(String statement, List<OptionDTO> options, boolean isMultipleChoice) {
        Set<String> distinctTexts = options.stream()
                .map(opt -> opt.text().trim().toLowerCase())
                .collect(Collectors.toSet());

        if (distinctTexts.size() < options.size()) {
            throw new ResourceIllegalArgumentException("As alternativas não podem ser repetidas.");
        }

        String normalizedStatement = statement.trim().toLowerCase();
        if (distinctTexts.contains(normalizedStatement)) {
            throw new ResourceIllegalArgumentException("Nenhuma alternativa pode ser igual ao enunciado da atividade.");
        }

        long correctCount = options.stream().filter(OptionDTO::isCorrect).count();
        long incorrectCount = options.size() - correctCount;

        if (isMultipleChoice) {
            if (correctCount < 2 || incorrectCount < 1) {
                throw new ResourceIllegalArgumentException("Atividades de múltipla escolha devem ter no mínimo duas corretas e pelo menos uma incorreta.");
            }
        } else {
            if (correctCount != 1) {
                throw new ResourceIllegalArgumentException("Deve haver exatamente uma opção correta.");
            }
        }
    }

    private List<Option> mapOptions(List<OptionDTO> dtoList, Task task) {
        return dtoList.stream()
                .map(opt -> new Option(null, opt.text(), opt.isCorrect(), task))
                .toList();
    }




}