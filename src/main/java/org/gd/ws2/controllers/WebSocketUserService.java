package org.gd.ws2.controllers;


import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class WebSocketUserService {
    private final Set<String> connectedUsers = new HashSet<>();

    public void addUserslist(SimpMessageHeaderAccessor headerAccessor){
        Object usernameObject = headerAccessor
                .getSessionAttributes()
                .get("username");

        if (usernameObject != null) {
            connectedUsers.add(usernameObject.toString());
        }

    }
    public void removeUserslist(SimpMessageHeaderAccessor headerAccessor){
        Object usernameObject = headerAccessor
                .getSessionAttributes()
                .get("username");

        if (usernameObject != null) {
            connectedUsers.remove(usernameObject.toString());
        }
    }
    public Set<String> getConnectedUsers() {
        return connectedUsers;
    }
}
