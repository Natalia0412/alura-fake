package br.com.alura.AluraFake.course.dto;

import br.com.alura.AluraFake.course.model.Status;
import br.com.alura.AluraFake.user.dto.UserSummaryDTO;

import java.time.LocalDateTime;

public record CoursePublishedDTO(
        Long id,
        LocalDateTime createdAt,
        String title,
        String description,
        UserSummaryDTO instructor,
        Status status,
        LocalDateTime publishedAt
) {
}
