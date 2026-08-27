package org.gd.ws2.config;

import lombok.RequiredArgsConstructor;
import org.gd.ws2.Service.JwtService;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {


    private final JwtService jwtService;
    @Override
    public @Nullable Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        System.out.println(
                "INTERCEPTOR RECEIVED: " +
                        accessor.getCommand()
        );
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader =
                    accessor.getFirstNativeHeader("Authorization");


            if (authHeader == null && !authHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException(
                        "Missing JWT token"
                );
            }
            String token = authHeader.substring(7);

            if (!jwtService.isTokenValid(token)) {

                throw new IllegalArgumentException(
                        "Invalid JWT token"
                );
        }
            String username =
                    jwtService.extractUsername(token);

            accessor.setUser(
                    () -> username
            );

            System.out.println(
                    "WebSocket authenticated: " +
                            username
            );

    }return message;
}}
