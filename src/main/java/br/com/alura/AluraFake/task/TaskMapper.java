package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.task.dto.OpenTextTaskRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @SubclassMapping(source = OpenTextTask.class, target = OpenTextTaskRequestDTO.class)
    OpenTextTaskRequestDTO toDTO(OpenTextTask task); // mapeia entidade para DTO base

    OpenTextTask toEntity(OpenTextTaskRequestDTO dto); // mapeia DTO para entidade
}
