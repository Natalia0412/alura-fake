package br.com.alura.AluraFake.task;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Option {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, length = 80)
        private String option;

        @Column(nullable = false)
        private boolean isCorrect;

        @ManyToOne(optional = false)
        private Task task;




}
