package org.gd.ws2.Entity;



import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    private String sender;
    private String recipient;
    private String content;
    private MessageType messageType;
    private LocalDateTime timestamp;
}