package org.gd.ws2.Service;

import org.gd.ws2.Entity.Conversation;
import org.gd.ws2.Entity.User;
import org.gd.ws2.repository.ConversationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConversationServiceTest {
 private  ConversationService conversationService;
    private ConversationRepository conversationRepository;
    private User user1;
    private User user2;
    @BeforeEach
    void setUp() {
        conversationRepository =
                mock(ConversationRepository.class);

        conversationService =
                new ConversationService(conversationRepository);

        user1 = new User();
        user1.setId(1L);
        user1.setUsername("GD");

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("Rahul");
 }
 @Test
    void shouldReturnExistingConversation()
    {
        Conversation exsitingConversation = new Conversation();
        when(conversationRepository.findConversation(user1,user2)).thenReturn(Optional.of(exsitingConversation));
        Conversation result = conversationService.getOrCreateConversation(user1,user2);
        assertSame(exsitingConversation,result);
        verify(conversationRepository).findConversation(user1,user2);
        verify(conversationRepository,never()).save(any(Conversation.class));
    }
    @Test
    void shouldCreateConversationWhenNoneExists(){

        when(conversationRepository.findConversation(user1,user2)).thenReturn(Optional.empty());
        Conversation newConversation =
                new Conversation();
        when(conversationRepository.save(
                any(Conversation.class)
        )).thenReturn(newConversation);
        Conversation result =
                conversationService.getOrCreateConversation(
                        user1,
                        user2
                );
        assertSame(
                newConversation,
                result
        );
        verify(conversationRepository)
                .findConversation(user1, user2);

        ArgumentCaptor<Conversation> captor =
                ArgumentCaptor.forClass(Conversation.class);

        verify(conversationRepository)
                .save(captor.capture());

        Conversation savedConversation =
                captor.getValue();

        assertTrue(
                savedConversation.getMembers().contains(user1)
        );

        assertTrue(
                savedConversation.getMembers().contains(user2)
        );
    }


}