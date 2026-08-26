package org.gd.ws2.Entity;

import jakarta.persistence.*;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToMany
    @JoinTable(
            name="conversation_members",
            joinColumns = @JoinColumn(name = "coversation_id"),
            inverseJoinColumns  = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();

}
