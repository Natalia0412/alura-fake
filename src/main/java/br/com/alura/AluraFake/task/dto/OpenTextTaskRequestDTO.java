package br.com.alura.AluraFake.task.dto;

public record OpenTextTaskRequestDTO( Long courseId,
                                      String statement,
                                      Integer order) implements  TaskDTO{
}
