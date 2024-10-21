package blog.posts.p1.authentication.userManagement.mapper;

import blog.posts.p1.authentication.userManagement.dto.Response.UserResponseDto;
import blog.posts.p1.authentication.userManagement.dto.Request.UserRequestDto;
import blog.posts.p1.authentication.userManagement.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRequestDto userRequestDto);
    UserResponseDto toDto(User user);
    List<UserResponseDto> toDto(List<User> users);
    User toEntity(UserResponseDto userResponseDto);
}
