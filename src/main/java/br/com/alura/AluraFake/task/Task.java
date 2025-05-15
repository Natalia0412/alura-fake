package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@DiscriminatorColumn(name = "task_type")
@Getter
@Setter
@ToString
@NoArgsConstructor
@MappedSuperclass
public abstract class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    protected String statement;

    protected Integer order;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    protected LocalDateTime createdAt = LocalDateTime.now();
}
