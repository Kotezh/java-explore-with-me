package ewm.user.service;

import ewm.exception.NotFoundException;
import ewm.user.User;
import ewm.user.UserMapper;
import ewm.user.UserRepository;
import ewm.user.dto.UserCreateDto;
import ewm.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserCreateDto userDto) {
        User user = userMapper.mapCreateDtoToModel(userDto);
        return userMapper.mapModelToDto(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User не найден"));
    }

    @Override
    public void delete(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User не найден");
        }
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> userIds, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (userIds != null) {
            return userRepository.findAllByIdIn(userIds, pageable).stream().map(userMapper::mapModelToDto).collect(Collectors.toList());
        } else {
            return userRepository.findAll(pageable).map(userMapper::mapModelToDto).toList();
        }
    }
}
