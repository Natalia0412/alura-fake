package br.com.alura.AluraFake.task.dto;
import jakarta.validation.constraints.NotBlank;
public record OptionDTO(
        @NotBlank(message = "Texto da opção é obrigatório")
        String text,

        boolean isCorrect
) {
}
