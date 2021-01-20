package social.network.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import social.network.dto.MessageSendRequestDto;
import social.network.exception.SocialNetworkException;
import social.network.model.SuccessfulResponse;
import social.network.security.jwt.JwtUtils;
import social.network.service.MessageService;

import javax.validation.Valid;
import java.security.Principal;
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
    public ResponseEntity<?> sendMessage(
            Principal principal,
            @RequestBody @Valid MessageSendRequestDto messageSendRequestDto) throws SocialNetworkException {
        messageService.sendMessage(principal.getName(), messageSendRequestDto);
        return ResponseEntity.ok(new SuccessfulResponse("message was received!"));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/find-current-user-sent-messages")
    public Set<MessageSendRequestDto> findAllSentMessagesOfCurrentUser(
            Principal principal) throws SocialNetworkException {
        return messageService.findAllSentMessagesOfCurrentUser(principal.getName());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/find-current-user-received-messages")
    public Set<MessageSendRequestDto> findAllReceivedMessagesOfCurrentUser(
            Principal principal) throws SocialNetworkException {
        return messageService.findAllReceivedMessagesOfCurrentUser(principal.getName());
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
