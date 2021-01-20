package social.network.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import social.network.dto.MessageSendRequestDto;
import social.network.exception.SocialNetworkException;
import social.network.mapper.MessageMapper;
import social.network.model.ErrorCode;
import social.network.model.Message;
import social.network.model.User;
import social.network.repository.MessageRepository;
import social.network.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;


    public MessageService(MessageRepository messageRepository, UserRepository userRepository, MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messageMapper = messageMapper;
    }

    public void sendMessage(String username, MessageSendRequestDto messageSendRequestDto) throws SocialNetworkException {
        User user = userRepository.findUserByUsername(username);
        Message message = messageMapper.toEntity(messageSendRequestDto);
        message.setFromUser(user);
        messageRepository.save(message);
        log.trace("message sent! {}", message);
    }

    public Set<MessageSendRequestDto> findAllSentMessagesOfCurrentUser(String username) throws SocialNetworkException {
        User user = userRepository.findUserByUsername(username);
        Set<Message> messageList = messageRepository.findAllMessagesByFromUserId(user.getId());
        Set<MessageSendRequestDto> messageSendRequestDtoList = new HashSet<>();
        if (messageList.size() == 0) {
            throw new SocialNetworkException(ErrorCode.NotExists, "You don't have any sent messages!");
        }
        for (Message message : messageList) {
            MessageSendRequestDto messageSendRequestDto = messageMapper.toDto(message);
            messageSendRequestDtoList.add(messageSendRequestDto);
        }
        return messageSendRequestDtoList;
    }

    public Set<MessageSendRequestDto> findAllReceivedMessagesOfCurrentUser(String username) throws SocialNetworkException {
        User user = userRepository.findUserByUsername(username);
        Set<Message> messageList = messageRepository.findAllMessagesByToUserId(user.getId());
        Set<MessageSendRequestDto> messageSendRequestDtoList = new HashSet<>();
        if (messageList.size() == 0) {
            throw new SocialNetworkException(ErrorCode.NotExists, "You don't have any received messages!");
        }
        for (Message message : messageList) {
            MessageSendRequestDto messageSendRequestDto = messageMapper.toDto(message);
            messageSendRequestDtoList.add(messageSendRequestDto);
        }
        return messageSendRequestDtoList;
    }

    public Set<MessageSendRequestDto> findAllSentMessages(Long id) throws SocialNetworkException {
        if (!userRepository.existsById(id)) {
            throw new SocialNetworkException(ErrorCode.NotExists, "User with id:" + id + " does not exists!");
        }
        Set<Message> messageList = messageRepository.findAllMessagesByFromUserId(id);
        if (messageList.size() == 0) {
            throw new SocialNetworkException(ErrorCode.NotExists, "User with id:" + id + " does not have any sent messages!");
        }
        Set<MessageSendRequestDto> messageSendRequestDtoList = new HashSet<>();
        for (Message message : messageList) {
            MessageSendRequestDto messageSendRequestDto = messageMapper.toDto(message);
            messageSendRequestDtoList.add(messageSendRequestDto);
        }
        return messageSendRequestDtoList;
    }

    public Set<MessageSendRequestDto> findAllReceivedMessages(Long id) throws SocialNetworkException {
        if (!userRepository.existsById(id)) {
            throw new SocialNetworkException(ErrorCode.NotExists, "User with id:" + id + " does not exists!");
        }
        Set<Message> messageList = messageRepository.findAllMessagesByToUserId(id);
        if (messageList.size() == 0) {
            throw new SocialNetworkException(ErrorCode.NotExists, "User with id:" + id + " does not have any received messages!");
        }
        Set<MessageSendRequestDto> messageSendRequestDtoList = new HashSet<>();
        for (Message message : messageList) {
            MessageSendRequestDto messageSendRequestDto = messageMapper.toDto(message);
            messageSendRequestDtoList.add(messageSendRequestDto);
        }
        return messageSendRequestDtoList;
    }

}
