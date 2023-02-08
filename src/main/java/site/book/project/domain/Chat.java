package site.book.project.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Entity(name = "CHAT")
@SequenceGenerator(name = "CHAT_SEQ_GEN", sequenceName = "CHAT_SEQ", initialValue = 1, allocationSize = 1)
public class Chat extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CHAT_SEQ_GEN")
    private Integer chatRoomId;
    
    @Column(nullable = false)
    private Integer sellerId;
    
    @Column(nullable = false)
    private Integer buyerId;
    
    @Column(nullable = false)
    private Integer usedBookId;
    
    @Column
    private String fileName;
    
    public Chat update(String fileName) {
        this.fileName = fileName;
        return this;
    }
    
}
