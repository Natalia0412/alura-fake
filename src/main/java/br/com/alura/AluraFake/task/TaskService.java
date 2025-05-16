package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseService;
import br.com.alura.AluraFake.task.dto.OpenTextTaskRequestDTO;
import br.com.alura.AluraFake.util.error.ResourceIllegalStateException;
import br.com.alura.AluraFake.util.error.ResourceNotFoundException;
import br.com.alura.AluraFake.util.error.ResourceIllegalArgumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TaskService {
    private final CourseService courseService;
    private final TaskRepository taskRepository;
    private final TaskMapper mapper;

    public OpenTextTask createOpenTextTask(OpenTextTaskRequestDTO openTextTaskRequestDTO){
        Long courseIdFromOpenTextTask = openTextTaskRequestDTO.courseId();

        Course course = courseService.findCourseById(courseIdFromOpenTextTask);

        if (!"BUILDING".equals(course.getStatus())) {
            throw new ResourceIllegalStateException("Curso não está em BUILDING");
        }

        if (openTextTaskRequestDTO.statement().length() < 4 || openTextTaskRequestDTO.statement().length() > 255) {
            throw new ResourceIllegalArgumentException("Enunciado inválido");
        }

        if (taskRepository.existsByCourseAndStatement(course,  openTextTaskRequestDTO.statement())) {
            throw new ResourceIllegalArgumentException("Enunciado duplicado no mesmo curso");
        }

        if ( openTextTaskRequestDTO.order() == null ||  openTextTaskRequestDTO.order() < 1) {
            throw new ResourceIllegalArgumentException("A ordem deve ser um número positivo");
        }

        int quantityActivities = taskRepository.countByCourse(course);
        int nextValidOrder = quantityActivities + 1;


        if (openTextTaskRequestDTO.order() > nextValidOrder) {
            throw new ResourceIllegalArgumentException("Ordem inválida: há lacuna na sequência. Última ordem válida é " + quantityActivities);
        }

        if (openTextTaskRequestDTO.order() <= quantityActivities) {
            taskRepository.shiftOrderFrom(course, openTextTaskRequestDTO.order());
        }

        OpenTextTask entity = mapper.toEntity(openTextTaskRequestDTO);
        entity.setCourse(course);

        return taskRepository.save(entity);

    }




}
