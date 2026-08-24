package org.gd.ws2.config;

import org.jspecify.annotations.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    @Override
    public @Nullable Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        System.out.println(
                "INTERCEPTOR RECEIVED: " +
                        accessor.getCommand()
        );
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String username =
                    accessor.getFirstNativeHeader("username");


            if (username != null && !username.isBlank()) {
                accessor.setUser(() -> username);
            }
        }
        return message;
    }
}
