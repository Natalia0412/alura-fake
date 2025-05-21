package br.com.alura.AluraFake.course.mapper;

import br.com.alura.AluraFake.course.dto.CoursePublishedDTO;
import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.user.dto.UserSummaryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    @Mapping(target = "instructor", source = "instructor", qualifiedByName = "toSummary")
    CoursePublishedDTO toCoursePublishedDTO(Course course);

    @Named("toSummary")
    default UserSummaryDTO toSummary(br.com.alura.AluraFake.user.model.User user) {
        return new UserSummaryDTO(user.getName(), user.getRole().name(), user.isInstructor());
    }
}
