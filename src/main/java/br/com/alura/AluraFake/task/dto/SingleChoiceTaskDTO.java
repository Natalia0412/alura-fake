package br.com.alura.AluraFake.task.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SingleChoiceTaskDTO(
        @NotNull(message = "courseId é obrigatório")
        Long courseId,

        @NotBlank(message = "O enunciado é obrigatório")
        @Size(min = 4, max = 255, message = "O enunciado deve ter entre 4 e 255 caracteres")
        String statement,

        @NotNull(message = "Ordem é obrigatória")
        @Positive(message = "A ordem deve ser um número positivo")
        Integer order,

        @Size(min = 2, max = 5, message = "Deve haver no mínimo 2 e no maximo 6 opções")
        List<@Valid OptionDTO> options
) {

}
