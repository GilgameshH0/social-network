package social.network.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import social.network.dto.MessageSendRequestDto;
import social.network.exception.SocialNetworkException;
import social.network.model.SuccessfulResponse;
import social.network.service.MessageService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/send-message")
    public ResponseEntity<?> sendMessage(@RequestHeader("Authorization") String bearer, @RequestBody @Valid MessageSendRequestDto messageSendRequestDto) throws SocialNetworkException {
        messageService.sendMessage(bearer, messageSendRequestDto);
        return ResponseEntity.ok(new SuccessfulResponse("message was received!"));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/find-current-user-sent-messages")
    public Set<MessageSendRequestDto> findAllSentMessagesOfCurrentUser(@RequestHeader("Authorization") String bearer) throws SocialNetworkException {
        return messageService.findAllSentMessagesOfCurrentUser(bearer);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/find-current-user-received-messages")
    public Set<MessageSendRequestDto> findAllReceivedMessagesOfCurrentUser(@RequestHeader("Authorization") String bearer) throws SocialNetworkException {
        return messageService.findAllReceivedMessagesOfCurrentUser(bearer);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/find-sent-messages/{id}")
    public Set<MessageSendRequestDto> findAllSentMessages(@PathVariable("id") Long id) throws SocialNetworkException {
        return messageService.findAllSentMessages(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/find-received-messages/{id}")
    public Set<MessageSendRequestDto> findAllReceivedMessages(@PathVariable("id") Long id) throws SocialNetworkException {
        return messageService.findAllReceivedMessages(id);
    }
}
