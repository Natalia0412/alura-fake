package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.util.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CourseService {
    private final CourseRepository courseRepository;

    public Course findCourseById(Long courseId){
        Optional<Course> obj = courseRepository.findById(courseId);
        if(obj.isEmpty()) throw new ResourceNotFoundException("Curso não encontrado");
        return obj.get();
    }
}
