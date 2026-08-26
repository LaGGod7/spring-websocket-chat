package org.gd.ws2.repository;

import org.gd.ws2.Entity.Conversation;
import org.gd.ws2.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation,Long> {

    @Query("""
    SELECT c FROM Conversation c
        WHERE :user1 MEMBER OF c.members
          AND :user2 MEMBER OF c.members""")
    Optional<Conversation> findConversation(User user1,
                                            User user2);

}
