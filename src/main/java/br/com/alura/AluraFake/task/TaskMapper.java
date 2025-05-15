package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.task.dto.OpenTextTaskRequestDTO;
import br.com.alura.AluraFake.task.dto.TaskDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.SubclassMapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.jpa.repository.JpaContext;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    @SubclassMapping(source = OpenTextTask.class, target = OpenTextTaskRequestDTO.class)
    TaskDTO toDTO(Task task);

    OpenTextTask toEntity(OpenTextTaskRequestDTO dto);
}
