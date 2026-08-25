package org.gd.ws2.repository;


import org.gd.ws2.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<Message,Long> {
    @Query("""
    SELECT m FROM Message m
    WHERE
        (m.sender = :sender AND m.recipient = :recipient)
        OR
        (m.sender = :recipient AND m.recipient = :sender)
    ORDER BY m.timestamp ASC
""")
    List<Message> findConversation(@Param("sender") String sender,
                                   @Param("recipient") String recipient);
}
