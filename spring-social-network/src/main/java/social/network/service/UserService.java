package social.network.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import social.network.dto.UserDTO;
import social.network.exception.EmailAlreadyInUseException;
import social.network.exception.UserDoesNotExistsException;
import social.network.exception.UsernameAlreadyInUseException;
import social.network.exception.WrongBearerException;
import social.network.mapper.UserMapper;
import social.network.model.*;
import social.network.dto.auth.LogInUser;
import social.network.dto.auth.SignUpUser;
import social.network.repository.RoleRepository;
import social.network.repository.UserRepository;
import social.network.security.jwt.JwtUtils;
import social.network.security.service.UserDetailsImpl;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;

    public UserService(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils, UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.userMapper = userMapper;
    }

    public void registerUser(SignUpUser signUpUser) throws UsernameAlreadyInUseException, EmailAlreadyInUseException {
        if (userRepository.existsByUsername(signUpUser.getUsername())) {
           throw new UsernameAlreadyInUseException(signUpUser.getUsername());
        }
        if (userRepository.existsByEmail(signUpUser.getEmail())) {
            throw new EmailAlreadyInUseException();
        }
        User user = new User(
                signUpUser.getUsername(),
                encoder.encode(signUpUser.getPassword()),
                signUpUser.getName(),
                signUpUser.getSurname(),
                signUpUser.getPatronymic(),
                signUpUser.getGender(),
                signUpUser.getBirthdate(),
                signUpUser.getCountry(),
                signUpUser.getEmail());
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER);
        roles.add(userRole);
        user.setRoles(roles);
        userRepository.save(user);
        log.trace("User registered successfully! {}", user.toString());
    }

    public JwtResponse authenticateUser(LogInUser logInUser) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(logInUser.getUsername(), logInUser.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        log.trace(logInUser.getUsername() + " successfully created token for user " + logInUser.getUsername() + "! {}", jwt);
        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail());
    }

    public UserDTO findUser(Long id) throws UserDoesNotExistsException {
        if (!userRepository.existsById(id)) {
            throw new UserDoesNotExistsException(id);
        }
        return userMapper.toDto(userRepository.findUserById(id));
    }

    public void updateCurrentUser(String bearer, SignUpUser signUpUser) throws WrongBearerException {
        String username = jwtUtils.getUsernameFromTokenString(bearer);
        if (!userRepository.existsByUsername(username)){
            throw new WrongBearerException(bearer);
        }
        User user = userRepository.findUserByUsername(username);
        updateUserInRepository(signUpUser, user);
        log.trace(username + " successfully updated his/her account!");
    }

    public void updateUser(SignUpUser signUpUser, Long id) throws UserDoesNotExistsException {
        if (!userRepository.existsById(id)) {
            throw new UserDoesNotExistsException(id);
        }
        User user = userRepository.findUserById(id);
        updateUserInRepository(signUpUser, user);
        log.trace("data successfully updated! {}", user.toString());
    }

    @Transactional
    public void removeCurrentUser(String bearer) throws WrongBearerException {
        String username = jwtUtils.getUsernameFromTokenString(bearer);
        if (!userRepository.existsByUsername(username)){
            throw new WrongBearerException(bearer);
        }
        userRepository.removeUserByUsername(username);
        log.trace(username + " is successfully deleted his/her account");
    }

    @Transactional
    public void removeUser(Long id) throws UserDoesNotExistsException {
        if (!userRepository.existsById(id)) {
            throw new UserDoesNotExistsException(id);
        }
        userRepository.removeUserById(id);
        log.trace("User with id:"+ id + " deleted successfully!");
    }

    public List<UserDTO> findAllUserByCountry(String country){
        List<User> userList = userRepository.findAllByCountry(country);
        List<UserDTO> userDTOList = new ArrayList<>();
        for (User user : userList) {
            UserDTO userDTO = userMapper.toDto(user);
            userDTOList.add(userDTO);
        }
        return userDTOList;
    }

    private void updateUserInRepository(SignUpUser signUpUser, User user) {
        user.setUsername(signUpUser.getUsername());
        user.setPassword(signUpUser.getPassword());
        user.setName(signUpUser.getName());
        user.setSurname(signUpUser.getSurname());
        user.setPatronymic(signUpUser.getPatronymic());
        user.setGender(signUpUser.getGender());
        user.setBirthdate(signUpUser.getBirthdate());
        user.setCountry(signUpUser.getCountry());
        user.setEmail(signUpUser.getEmail());
        userRepository.save(user);
    }
}

