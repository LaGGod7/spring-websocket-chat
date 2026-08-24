package org.gd.ws2.controllers;

import lombok.RequiredArgsConstructor;
import org.gd.ws2.Entity.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.security.Principal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@org.springframework.stereotype.Controller
public class Controller {
    private final WebSocketUserService userService;
    private final SimpMessageSendingOperations messagingTemplate;
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public Message sendMessage(@Payload Message message){
        message.setTimestamp(LocalDateTime.now());
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
//        System.out.println("CURRENT USER: " + principal.getName());
//        System.out.println("RECIPIENT: " + message.getRecipient());
        message.setTimestamp(LocalDateTime.now());
            messagingTemplate.convertAndSendToUser(message.getRecipient(),"/queue/message",message);
    }




}
