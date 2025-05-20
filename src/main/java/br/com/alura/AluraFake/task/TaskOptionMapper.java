package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.task.dto.OptionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskOptionMapper {

    @Mapping(target = "option", source = "text")
    TaskOption toEntity(OptionDTO optionDTO);

    @Mapping(target = "text", source = "option")
    @Mapping(target = "isCorrect", expression = "java(option.isCorrect())")
    OptionDTO toDto(TaskOption option);

    default List<TaskOption> toEntityList(List<OptionDTO> list) {
        return list.stream().map(this::toEntity).toList();
    }

    default List<OptionDTO> toDtoList(List<TaskOption> list) {
        return list.stream().map(this::toDto).toList();
    }


}
