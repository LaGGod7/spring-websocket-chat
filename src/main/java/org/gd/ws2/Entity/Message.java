package org.gd.ws2.Entity;

import lombok.*;

import java.awt.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    private String sender;
    private String content;
    private String recipient;
    private MessageType messageType;

}
