package social.network.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import social.network.dto.MessageDTO;
import social.network.exception.UserDoesNotExistsException;
import social.network.exception.WrongBearerException;
import social.network.mapper.MessageMapper;
import social.network.model.Message;
import social.network.model.User;
import social.network.repository.MessageRepository;
import social.network.repository.UserRepository;
import social.network.security.jwt.JwtUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;
    private final JwtUtils jwtUtils;


    public MessageService(MessageRepository messageRepository, UserRepository userRepository, MessageMapper messageMapper, JwtUtils jwtUtils) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messageMapper = messageMapper;
        this.jwtUtils = jwtUtils;
    }

    public void sendMessage(String bearer, MessageDTO messageDTO) throws WrongBearerException {
        String username = jwtUtils.getUsernameFromTokenString(bearer);
        if (!userRepository.existsByUsername(username)){
            throw new WrongBearerException(bearer);
        }
        User user = userRepository.findUserByUsername(username);
        Message message = messageMapper.toEntity(messageDTO);
        message.setFromUserId(user.getId());
        messageRepository.save(message);
        log.trace("message sent! {}", message.toString());
    }

    public List<MessageDTO> findAllSentMessagesOfCurrentUser(String bearer) throws WrongBearerException {
        String username = jwtUtils.getUsernameFromTokenString(bearer);
        if (!userRepository.existsByUsername(username)){
            throw new WrongBearerException(bearer);
        }
        User user = userRepository.findUserByUsername(username);
        List<Message> messageList = messageRepository.findAllMessagesByFromUserId(user.getId());
        List<MessageDTO> messageDTOList = new ArrayList<>();
        for (Message message : messageList) {
            MessageDTO messageDTO = messageMapper.toDto(message);
            messageDTOList.add(messageDTO);
        }
        return messageDTOList;
    }

    public List<MessageDTO> findAllReceivedMessagesOfCurrentUser(String bearer) throws WrongBearerException {
        String username = jwtUtils.getUsernameFromTokenString(bearer);
        if (!userRepository.existsByUsername(username)){
            throw new WrongBearerException(bearer);
        }
        User user = userRepository.findUserByUsername(username);
        List<Message> messageList = messageRepository.findAllMessagesByToUserId(user.getId());
        List<MessageDTO> messageDTOList = new ArrayList<>();
        for (Message message : messageList) {
            MessageDTO messageDTO = messageMapper.toDto(message);
            messageDTOList.add(messageDTO);
        }
        return messageDTOList;
    }

    public List<MessageDTO> findAllSentMessages(Long id) throws UserDoesNotExistsException {
        if (!userRepository.existsById(id)) {
            throw new UserDoesNotExistsException(id);
        }
        List<Message> messageList = messageRepository.findAllMessagesByFromUserId(id);
        List<MessageDTO> messageDTOList = new ArrayList<>();
        for (Message message : messageList) {
            MessageDTO messageDTO = messageMapper.toDto(message);
            messageDTOList.add(messageDTO);
        }
        return messageDTOList;
    }

    public List<MessageDTO> findAllReceivedMessages(Long id) throws UserDoesNotExistsException {
        if (!userRepository.existsById(id)) {
            throw new UserDoesNotExistsException(id);
        }
        List<Message> messageList = messageRepository.findAllMessagesByToUserId(id);
        List<MessageDTO> messageDTOList = new ArrayList<>();
        for (Message message : messageList) {
            MessageDTO messageDTO = messageMapper.toDto(message);
            messageDTOList.add(messageDTO);
        }
        return messageDTOList;
    }
}
