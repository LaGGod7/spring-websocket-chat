package org.gd.ws2.Service;

import lombok.RequiredArgsConstructor;
import org.gd.ws2.Entity.Conversation;
import org.gd.ws2.Entity.User;
import org.gd.ws2.repository.ConversationRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository conversationRepository;

    public Conversation getOrCreateConversation(User user1, User user2){
        return conversationRepository.findConversation(user1,user2).orElseGet(()->{
            Conversation conversation = new Conversation();
            conversation.setMembers(new HashSet<>(Set.of(user1,user2)
            ));
            return conversationRepository
                    .save(conversation);});

    }

}
