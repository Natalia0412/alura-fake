package br.com.alura.AluraFake.user.dto;

public record UserSummaryDTO(
        String name,
        String role,
        boolean instructor
) {
}
