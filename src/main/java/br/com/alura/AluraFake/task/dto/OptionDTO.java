package br.com.alura.AluraFake.task.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OptionDTO(
        @NotBlank(message = "Texto da opção é obrigatório")
        @Size(min = 4, max = 80, message = "Texto da opção deve ter entre 4 e 80 caracteres")
        String text,

        @NotBlank(message = "isCorrect da opção é obrigatório")
        boolean isCorrect
) {
}
