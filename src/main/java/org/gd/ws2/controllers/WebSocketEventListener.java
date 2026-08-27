package org.gd.ws2.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gd.ws2.Entity.ChatMessage;
import org.gd.ws2.Entity.Message;
import org.gd.ws2.Entity.MessageType;
import org.gd.ws2.repository.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {
    private final SimpMessageSendingOperations messagingTemplate;
    private final WebSocketUserService userService;
    private final UserRepository userRepository;

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        Object usernameObject = headerAccessor
                .getSessionAttributes()
                .get("username");
        if (usernameObject == null) {
            return;
        }
        String username = usernameObject.toString();

        log.info("Disconnected from " + username);

        userService.removeUserslist(headerAccessor);
        messagingTemplate.convertAndSend(
                "/topic/users",
                userService.getConnectedUsers()
        );
        ChatMessage response = new ChatMessage();
        response.setSender(username);
        response.setMessageType(MessageType.LEAVE);




        messagingTemplate.convertAndSend(
                "/topic/public",
                response
        );

    }
}
