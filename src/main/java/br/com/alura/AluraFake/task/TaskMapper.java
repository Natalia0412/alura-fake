package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.task.dto.MultipleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.OpenTextTaskDTO;
import br.com.alura.AluraFake.task.dto.SingleChoiceTaskDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "options", ignore = true)
    Task toTask(OpenTextTaskDTO dto);


    @Mapping(target = "courseId", source = "course.id")
    OpenTextTaskDTO toDto(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "options", ignore = true)
    Task singleDtoToTask(SingleChoiceTaskDTO dto);

    @Mapping(target = "courseId", source = "course.id")
    SingleChoiceTaskDTO toSingDto(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "options", ignore = true)
    Task multipleChoiseDtoToTask(MultipleChoiceTaskDTO dto);

    MultipleChoiceTaskDTO taskToMultiplechoiceDto(Task task);
}
