package br.com.alura.AluraFake.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record OpenTextTaskDTO(
        @NotNull(message = "courseId é obrigatório")
        Long courseId,

        @NotBlank(message = "O enunciado é obrigatório")
        @Size(min = 4, max = 255, message = "O enunciado deve ter entre 4 e 255 caracteres")
        String statement,

        @NotNull(message = "Ordem é obrigatória")
        @Positive(message = "A ordem deve ser um número positivo")
        Integer order
) {
}
