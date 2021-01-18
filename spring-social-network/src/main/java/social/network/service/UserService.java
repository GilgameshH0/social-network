package social.network.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import social.network.dto.UserGetResponseDto;
import social.network.dto.UserLogInRequestDto;
import social.network.dto.UserSignUpAndUpdateRequestDto;
import social.network.exception.SocialNetworkException;
import social.network.mapper.UserMapper;
import social.network.model.*;
import social.network.repository.RoleRepository;
import social.network.repository.UserPostRepository;
import social.network.repository.UserRepository;
import social.network.security.jwt.JwtUtils;
import social.network.security.service.UserDetailsImpl;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserPostRepository userPostRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;

    public UserService(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, UserPostRepository userPostRepository, PasswordEncoder encoder, JwtUtils jwtUtils, UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userPostRepository = userPostRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.userMapper = userMapper;
    }

    public void registerUser(UserSignUpAndUpdateRequestDto userSignUpAndUpdateRequestDto) throws SocialNetworkException {
        if (userRepository.existsByUsername(userSignUpAndUpdateRequestDto.getUsername()) || userRepository.existsByEmail(userSignUpAndUpdateRequestDto.getEmail())) {
            throw new SocialNetworkException(ErrorCode.UsernameOrEmailAlreadyInUse, "Username or email are already in use!");
        }
        User user = new User(
                userSignUpAndUpdateRequestDto.getUsername(),
                encoder.encode(userSignUpAndUpdateRequestDto.getPassword()),
                userSignUpAndUpdateRequestDto.getName(),
                userSignUpAndUpdateRequestDto.getSurname(),
                userSignUpAndUpdateRequestDto.getPatronymic(),
                userSignUpAndUpdateRequestDto.getGender(),
                userSignUpAndUpdateRequestDto.getBirthdate(),
                userSignUpAndUpdateRequestDto.getCountry(),
                userSignUpAndUpdateRequestDto.getEmail());
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER);
        roles.add(userRole);
        user.setRoles(roles);
        userRepository.save(user);
        log.trace("User registered successfully! {}", user);
    }

    public JwtResponse authenticateUser(UserLogInRequestDto userLogInRequestDto) throws SocialNetworkException {
        if (!userRepository.existsByUsername(userLogInRequestDto.getUsername())) {
            throw new SocialNetworkException(ErrorCode.UserDoesNotExists, "User with id:" + userLogInRequestDto.getUsername() + " does not exists!");
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLogInRequestDto.getUsername(), userLogInRequestDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        log.trace(userLogInRequestDto.getUsername() + " successfully created token for user " + userLogInRequestDto.getUsername() + "! {}", jwt);
        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail());
    }

    public UserGetResponseDto findUser(Long id) throws SocialNetworkException {
        if (!userRepository.existsById(id)) {
            throw new SocialNetworkException(ErrorCode.UserDoesNotExists, "User with id:" + id + " does not exists!");
        }
        return userMapper.toDto(userRepository.findUserById(id));
    }

    public void updateCurrentUser(String username, UserSignUpAndUpdateRequestDto userSignUpAndUpdateRequestDto) throws SocialNetworkException {
        User user = userRepository.findUserByUsername(username);
        updateUserInRepository(userSignUpAndUpdateRequestDto, user);
        log.trace(username + " successfully updated his/her account!");
    }

    public void updateUser(UserSignUpAndUpdateRequestDto userSignUpAndUpdateRequestDto, Long id) throws SocialNetworkException {
        if (!userRepository.existsById(id)) {
            throw new SocialNetworkException(ErrorCode.UserDoesNotExists, "User with id:" + id + " does not exists!");
        }
        User user = userRepository.findUserById(id);
        updateUserInRepository(userSignUpAndUpdateRequestDto, user);
        log.trace("data successfully updated! {}", user.toString());
    }

    @Transactional
    public void removeCurrentUser(String username) throws SocialNetworkException {
        User user = userRepository.findUserByUsername(username);
        userPostRepository.removeAllByOwner(user);
        log.trace("All posts from user with username: " + username + " successfully removed!");
        userRepository.removeUserByUsername(username);
        log.trace(username + " is successfully deleted his/her account");
    }

    @Transactional
    public void removeUser(Long id) throws SocialNetworkException {
        if (!userRepository.existsById(id)) {
            throw new SocialNetworkException(ErrorCode.UserDoesNotExists, "User with id:" + id + " does not exists!");
        }
        User user = userRepository.findUserById(id);
        userPostRepository.removeAllByOwner(user);
        log.trace("All posts from user with id: " + id + " successfully removed!");
        userRepository.removeUserById(id);
        log.trace("User with id:" + id + " deleted successfully!");
    }

    public Set<UserGetResponseDto> findAllUserByCountry(String country) throws SocialNetworkException {
        if (!userRepository.existsByCountry(country)) {
            throw new SocialNetworkException(ErrorCode.CountryDoesNotExists, "Country with name:" + country + " does not exists!");
        }
        Set<User> userList = userRepository.findAllByCountry(country);
        Set<UserGetResponseDto> userGetResponseDtoList = new HashSet<>();
        for (User user : userList) {
            UserGetResponseDto userGetResponseDto = userMapper.toDto(user);
            userGetResponseDtoList.add(userGetResponseDto);
        }
        return userGetResponseDtoList;
    }

    private void updateUserInRepository(UserSignUpAndUpdateRequestDto userSignUpAndUpdateRequestDto, User user) throws SocialNetworkException {
        if (userRepository.existsByUsername(userSignUpAndUpdateRequestDto.getUsername()) || userRepository.existsByEmail(userSignUpAndUpdateRequestDto.getEmail())){
            throw new SocialNetworkException(ErrorCode.UsernameOrEmailAlreadyInUse, "Username or email are already in use!");
        }
        user.setUsername(userSignUpAndUpdateRequestDto.getUsername());
        user.setPassword(encoder.encode(userSignUpAndUpdateRequestDto.getPassword()));
        user.setName(userSignUpAndUpdateRequestDto.getName());
        user.setSurname(userSignUpAndUpdateRequestDto.getSurname());
        user.setPatronymic(userSignUpAndUpdateRequestDto.getPatronymic());
        user.setGender(userSignUpAndUpdateRequestDto.getGender());
        user.setBirthdate(userSignUpAndUpdateRequestDto.getBirthdate());
        user.setCountry(userSignUpAndUpdateRequestDto.getCountry());
        user.setEmail(userSignUpAndUpdateRequestDto.getEmail());
        userRepository.save(user);
    }
}

