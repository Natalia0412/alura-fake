package br.com.alura.AluraFake.task.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "task_option")
public class TaskOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80, name = "option_text")
    private String option;

    @ManyToOne(optional = false)
    private Task task;

    @Column(nullable = false)
    private boolean isCorrect;

}
