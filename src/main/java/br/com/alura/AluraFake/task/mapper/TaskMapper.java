package br.com.alura.AluraFake.task.mapper;

import br.com.alura.AluraFake.task.dto.MultipleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.OpenTextTaskDTO;
import br.com.alura.AluraFake.task.dto.SingleChoiceTaskDTO;
import br.com.alura.AluraFake.task.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = TaskOptionMapper.class)
public interface TaskMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "course", ignore = true)
    Task toTask(OpenTextTaskDTO dto);


    @Mapping(target = "courseId", source = "course.id")
    OpenTextTaskDTO toDto(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "course", ignore = true)
    Task singleDtoToTask(SingleChoiceTaskDTO dto);

    @Mapping(target = "courseId", source = "course.id")
//    @Mapping(target = "option", source = "text")
    SingleChoiceTaskDTO toSingDto(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "course", ignore = true)
    Task multipleChoiseDtoToTask(MultipleChoiceTaskDTO dto);

    @Mapping(target = "courseId", source = "course.id")
//    @Mapping(target = "option", source = "text")
    MultipleChoiceTaskDTO taskToMultiplechoiceDto(Task task);
}
