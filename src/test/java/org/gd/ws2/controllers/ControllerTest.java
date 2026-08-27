package org.gd.ws2.controllers;

import static org.junit.jupiter.api.Assertions.*;

import org.gd.ws2.Entity.*;
import org.gd.ws2.Service.ConversationService;
import org.gd.ws2.repository.ChatMessageRepository;
import org.gd.ws2.repository.ConversationRepository;
import org.gd.ws2.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import java.security.Principal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ControllerTest {
    private ChatMessageRepository chatMessageRepository;
    private WebSocketUserService userService;
    private SimpMessageSendingOperations messagingTemplate;
    private UserRepository userRepository;
    private ConversationService conversationService;
    private ConversationRepository conversationRepository;

    private Controller controller;

    private User sender;
    private User recipient;
    private Conversation conversation;

    @BeforeEach
    void setUp() {

        chatMessageRepository =
                mock(ChatMessageRepository.class);

        userService =
                mock(WebSocketUserService.class);

        messagingTemplate =
                mock(SimpMessageSendingOperations.class);

        userRepository =
                mock(UserRepository.class);

        conversationService =
                mock(ConversationService.class);

        conversationRepository =
                mock(ConversationRepository.class);

        controller = new Controller(
                chatMessageRepository,
                userService,
                messagingTemplate,
                userRepository,
                conversationService,
                conversationRepository
        );

        sender = new User();
        sender.setId(1L);
        sender.setUsername("GD");

        recipient = new User();
        recipient.setId(2L);
        recipient.setUsername("Rahul");

        conversation = new Conversation();
    }
    @Test
    void shouldSavePrivateMessage() {

        ChatMessage message = new ChatMessage();

        message.setRecipient("Rahul");
        message.setContent("Hello Rahul");
        message.setMessageType(MessageType.CHAT);

        Principal principal = () -> "GD";

        when(userRepository.findByUsername("GD"))
                .thenReturn(Optional.of(sender));

        when(userRepository.findByUsername("Rahul"))
                .thenReturn(Optional.of(recipient));

        when(conversationService.getOrCreateConversation(
                sender,
                recipient
        )).thenReturn(conversation);

        controller.sendPrivateMessage(
                message,
                principal
        );

        ArgumentCaptor<Message> captor =
                ArgumentCaptor.forClass(Message.class);

        verify(chatMessageRepository)
                .save(captor.capture());

        Message savedMessage =
                captor.getValue();

        assertEquals(
                sender,
                savedMessage.getSender()
        );

        assertEquals(
                conversation,
                savedMessage.getConversation()
        );

        assertEquals(
                "Hello Rahul",
                savedMessage.getContent()
        );

        assertEquals(
                MessageType.CHAT,
                savedMessage.getMessageType()
        );

        assertNotNull(
                savedMessage.getTimestamp()
        );
    }
    @Test
    void shouldSendPrivateMessageToRecipientAndSender() {

        ChatMessage message = new ChatMessage();

        message.setRecipient("Rahul");
        message.setContent("Hello Rahul");
        message.setMessageType(MessageType.CHAT);

        Principal principal = () -> "GD";

        when(userRepository.findByUsername("GD"))
                .thenReturn(Optional.of(sender));

        when(userRepository.findByUsername("Rahul"))
                .thenReturn(Optional.of(recipient));

        when(conversationService.getOrCreateConversation(
                sender,
                recipient
        )).thenReturn(conversation);

        controller.sendPrivateMessage(
                message,
                principal
        );

        verify(messagingTemplate)
                .convertAndSendToUser(
                        eq("Rahul"),
                        eq("/queue/message"),
                        any(ChatMessage.class)
                );

        verify(messagingTemplate)
                .convertAndSendToUser(
                        eq("GD"),
                        eq("/queue/message"),
                        any(ChatMessage.class)
                );
    }


}