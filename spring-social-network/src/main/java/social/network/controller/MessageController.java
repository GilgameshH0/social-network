package social.network.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import social.network.dto.MessageDTO;
import social.network.exception.UserDoesNotExistsException;
import social.network.exception.WrongBearerException;
import social.network.service.MessageService;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/send-message")
    public ResponseEntity<?> sendMessage(@RequestHeader("Authorization") String bearer, @Valid @RequestBody MessageDTO messageDTO) throws WrongBearerException {
        messageService.sendMessage(bearer, messageDTO);
        return ResponseEntity.ok("message was received");
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/find-current-user-sent-messages")
    public List<MessageDTO> findAllSentMessagesOfCurrentUser(@RequestHeader("Authorization") String bearer) throws WrongBearerException {
        return messageService.findAllSentMessagesOfCurrentUser(bearer);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/find-current-user-received-messages")
    public List<MessageDTO> findAllReceivedMessagesOfCurrentUser(@RequestHeader("Authorization") String bearer) throws WrongBearerException {
        return messageService.findAllReceivedMessagesOfCurrentUser(bearer);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/find-sent-messages")
    public List<MessageDTO> findAllSentMessages(Long id) throws UserDoesNotExistsException {
        return messageService.findAllSentMessages(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/find-received-messages")
    public List<MessageDTO> findAllReceivedMessages(Long id) throws UserDoesNotExistsException {
        return messageService.findAllReceivedMessages(id);
    }
}
