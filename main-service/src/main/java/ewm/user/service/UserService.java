package ewm.user.service;

import ewm.user.User;
import ewm.user.dto.UserCreateDto;
import ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserCreateDto userDto);

    User getUserById(long userId);

    void delete(long userId);

    List<UserDto> getUsers(List<Long> userIds, Integer from, Integer size);
}
