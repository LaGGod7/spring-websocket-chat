package org.gd.ws2.controllers;

import lombok.RequiredArgsConstructor;
import org.gd.ws2.Entity.Message;
import org.gd.ws2.repository.ChatMessageRepository;
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

@RequiredArgsConstructor
@org.springframework.stereotype.Controller
public class Controller {
    private final ChatMessageRepository chatMessageRepository;
    private final WebSocketUserService userService;
    private final SimpMessageSendingOperations messagingTemplate;


    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public Message sendMessage(@Payload Message message){
        message.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(message);
        return message;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public Message addUser(@Payload Message message, SimpMessageHeaderAccessor headerAccessor){
        headerAccessor.getSessionAttributes().put("username", message.getSender());

        userService.addUserslist(headerAccessor);
        messagingTemplate.convertAndSend(
                "/topic/users",
                userService.getConnectedUsers()
        );

        return message;
    }
    @MessageMapping("/chat.privateMessage")
    public void sendPrivateMessage(@Payload Message message, Principal principal){
        Message newMessage = Message.builder()
                        .sender(message.getSender())
                                .recipient(message.getRecipient())
                                        .content(message.getContent())
                                                .messageType(message.getMessageType())
                                                        .timestamp(message.getTimestamp())
                                                                .build();
        chatMessageRepository.save(newMessage);

        message.setTimestamp(LocalDateTime.now());
            messagingTemplate.convertAndSendToUser(message.getRecipient(),"/queue/message",message);
    }
    @GetMapping("/messages/{user1}/{user2}")
    @ResponseBody
    public List<Message> getConversation(@PathVariable String user1, @PathVariable String user2){
        return chatMessageRepository.findConversation(user1,user2);

    }
    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "IT WORKS";
    }




}
