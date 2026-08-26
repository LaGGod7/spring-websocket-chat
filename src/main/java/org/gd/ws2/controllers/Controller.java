package org.gd.ws2.controllers;

import lombok.RequiredArgsConstructor;
import org.gd.ws2.Entity.ChatMessage;
import org.gd.ws2.Entity.Conversation;
import org.gd.ws2.Entity.Message;
import org.gd.ws2.Entity.User;
import org.gd.ws2.Service.ConversationService;
import org.gd.ws2.repository.ChatMessageRepository;
import org.gd.ws2.repository.ConversationRepository;
import org.gd.ws2.repository.UserRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@org.springframework.stereotype.Controller
public class Controller {
    private final ChatMessageRepository chatMessageRepository;
    private final WebSocketUserService userService;
    private final SimpMessageSendingOperations messagingTemplate;
    private final UserRepository userRepository;
    private final ConversationService  conversationService;
    private final ConversationRepository conversationRepository;


    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public void sendPublicMessage(@Payload ChatMessage message,Principal principal){
        User sender = userRepository
                .findByUsername(principal.getName())
                .orElseThrow();
        Message savedMessage = Message.builder()
                .sender(sender)
                .conversation(null)
                .content(message.getContent())
                .messageType(message.getMessageType())
                .timestamp(LocalDateTime.now())
                .build();

        chatMessageRepository.save(savedMessage);
        ChatMessage response = new ChatMessage();
        response.setSender(
                savedMessage.getSender().getUsername()
        );
        response.setRecipient(null);
        response.setContent(savedMessage.getContent());
        response.setMessageType(savedMessage.getMessageType());
        response.setTimestamp(savedMessage.getTimestamp());

        messagingTemplate.convertAndSend(
                "/topic/public",
                response
        );
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage  addUser(@Payload ChatMessage  message, SimpMessageHeaderAccessor headerAccessor, Principal principal){
        String username = principal.getName();

        headerAccessor.getSessionAttributes().put("username", username);

        User sender = userRepository
                .findByUsername(username)
                .orElseGet(() ->
                        userRepository.save(
                                new User(null, username)
                        )
                );


        userService.addUserslist(headerAccessor);

        messagingTemplate.convertAndSend(
                "/topic/users",
                userService.getConnectedUsers()
        );

        message.setSender(sender.getUsername());

        return message;
    }
    @MessageMapping("/chat.privateMessage")
    public void sendPrivateMessage(@Payload ChatMessage message, Principal principal){
        String username = principal.getName();
        User sender = userRepository
                .findByUsername(username)
                .orElseThrow();

        User recipient = userRepository
                .findByUsername(message.getRecipient())
                .orElseThrow();

        Conversation conversation =
                conversationService.getOrCreateConversation(
                        sender,
                        recipient
                );

        Message savedMessage = Message.builder()
                        .sender(sender)
                           .conversation(conversation)
                                        .content(message.getContent())
                                                .messageType(message.getMessageType())
                                                        .timestamp(message.getTimestamp())
                                                                .build();
        chatMessageRepository.save(savedMessage);
        ChatMessage response = new ChatMessage();


        response.setSender(savedMessage.getSender().getUsername());
        response.setRecipient(recipient.getUsername());
        response.setContent(savedMessage.getContent());
        response.setMessageType(savedMessage.getMessageType());
        response.setTimestamp(savedMessage.getTimestamp());

        message.setTimestamp(LocalDateTime.now());
            messagingTemplate.convertAndSendToUser(response.getRecipient(),"/queue/message", response);
    }
    @GetMapping("/messages/{user1}/{user2}")
    @ResponseBody
    public List<ChatMessage> getConversation(@PathVariable String user1, @PathVariable String user2){
        User sender = userRepository.findByUsername(user1).orElseThrow();
        User recipient = userRepository.findByUsername(user2).orElseThrow();
        Optional<Conversation> conversation =
                conversationRepository.findConversation(
                        sender,
                        recipient
                );
        return conversation.map(value -> chatMessageRepository
                .findByConversationOrderByTimestampAsc(value)
                .stream()
                .map(message -> {
                    ChatMessage dto = new ChatMessage();
                    dto.setSender(message.getSender().getUsername());

                    User otherUser = message.getConversation()
                            .getMembers()
                            .stream()
                            .filter(user ->
                                    !user.getId().equals(
                                            message.getSender().getId()
                                    )
                            )
                            .findFirst()
                            .orElseThrow();
                    dto.setRecipient(
                            otherUser.getUsername()
                    );
                    dto.setContent(message.getContent());
                    dto.setTimestamp(message.getTimestamp());
                    dto.setMessageType(message.getMessageType());
                    return dto;
                }).toList()).orElseGet(List::of);

    }
    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "IT WORKS";
    }




}
