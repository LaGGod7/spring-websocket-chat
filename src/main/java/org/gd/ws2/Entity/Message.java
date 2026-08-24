package org.gd.ws2.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.awt.*;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String recipient;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    private LocalDateTime timestamp;

}
