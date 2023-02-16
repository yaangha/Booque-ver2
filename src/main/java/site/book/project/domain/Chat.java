package site.book.project.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

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
public class Chat {

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
    
    @Column
    private LocalDateTime createdTime;
    
    @Column
    private LocalDateTime modifiedTime;    // BaseTimeEntity extend 안 하고 수동으로 넣어 줌(txt append될 때마다 수동으로 시간 업뎃시켜주기 위함)
    
    public Chat update(String fileName) {
        this.fileName = fileName;
        return this;
    }
    
}
