package site.book.project.domain;

import java.time.LocalDateTime;

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
@Entity(name = "ChatAssist")
@SequenceGenerator(name = "ChatAssist_SEQ_GEN", sequenceName = "ChatAssist_SEQ", initialValue = 1, allocationSize = 1)
public class ChatAssist {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ChatAssist_SEQ_GEN")
    private Integer Id;
    
    @Column(nullable = false)
    private Integer chatRoomId;
    
    @Column
    private Integer userId; 
    
    @Column
    private Integer readChk; // 읽었으면 0, 안읽었으면 1
    
    @Column
    private Integer readCount;
    
    @Column
    private String lastChat;
    
    @Column
    private LocalDateTime modifiedTime;
    
    public ChatAssist updateLastChat(String lastChat, LocalDateTime modifiedTime) {
        this.lastChat = lastChat;
        this.modifiedTime = modifiedTime;
        return this;
    }
    
    public ChatAssist updateReadChk(Integer userId, Integer readChk, Integer readCount) {
        this.userId = userId;
        this.readChk = readChk;
        this.readCount = readCount;
        return this;
    }
    
}
