package br.com.alura.AluraFake.task.service;

import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.repository.CourseRepository;
import br.com.alura.AluraFake.course.service.CourseService;
import br.com.alura.AluraFake.course.model.Status;
import br.com.alura.AluraFake.task.mapper.TaskMapper;
import br.com.alura.AluraFake.task.mapper.TaskOptionMapper;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import br.com.alura.AluraFake.task.model.Type;
import br.com.alura.AluraFake.task.dto.MultipleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.OpenTextTaskDTO;
import br.com.alura.AluraFake.task.dto.OptionDTO;
import br.com.alura.AluraFake.task.dto.SingleChoiceTaskDTO;
import br.com.alura.AluraFake.task.model.Task;
import br.com.alura.AluraFake.util.error.ResourceIllegalArgumentException;
import br.com.alura.AluraFake.util.error.ResourceIllegalStateException;
import br.com.alura.AluraFake.util.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TaskService {

    private final CourseRepository courseRepository;
    private final TaskRepository taskRepository;
    private final TaskMapper mapper;
    private final TaskOptionMapper taskOptionMapper;

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
        task.setOptions(
                taskOptionMapper.toEntityList(dto.options()).stream()
                        .peek(opt -> opt.setTask(task))
                        .toList()
        );
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
        task.setOptions(
                taskOptionMapper.toEntityList(dto.options()).stream()
                        .peek(opt -> opt.setTask(task))
                        .toList()
        );
        taskRepository.save(task);

        return mapper.taskToMultiplechoiceDto(task);

    }

    private Course validateCourseAndOrder(Long courseId, Integer order) {
        Course course = courseRepository.findById(courseId).orElseThrow(()-> new ResourceNotFoundException("Curso não encontrado"));
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



    public Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(() ->new ResourceNotFoundException("Task não encontrado"));
    }

    public List<Task> checkCourseHasTask (Course course){
        List<Task> tasks = taskRepository.findByCourseOrderByOrder(course);

        if (tasks.isEmpty()) {
            throw new ResourceIllegalStateException("Curso precisa de tarefas para ser publicado.");
        }

        return tasks;
    }
    public void checkCourseHasOneOfEachTypeOfTask( List<Task> tasks){


        boolean hasOpenText = tasks.stream().anyMatch(t -> t.getType() == Type.OPEN_TEXT);
        boolean hasSingleChoice = tasks.stream().anyMatch(t -> t.getType() == Type.SINGLE_CHOICE);
        boolean hasMultipleChoice = tasks.stream().anyMatch(t -> t.getType() == Type.MULTIPLE_CHOICE);

        if (!(hasOpenText && hasSingleChoice && hasMultipleChoice)) {
            throw new ResourceIllegalStateException("Curso precisa de todos os tipos de tarefas.");
        }
    }

}