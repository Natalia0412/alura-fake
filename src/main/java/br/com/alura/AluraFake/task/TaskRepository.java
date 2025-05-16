package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    boolean existsByCourseAndStatement(Course course, String statement);
    int countByCourse(Course course);
    @Modifying
    @Query("UPDATE Task t SET t.order = t.order + 1 WHERE t.course = :course AND t.order >= :order")
    void shiftOrderFrom(@Param("course") Course course, @Param("order") Integer order);
}
