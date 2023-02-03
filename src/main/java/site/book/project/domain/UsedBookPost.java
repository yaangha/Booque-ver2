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
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
@Entity(name = "USEDBOOKPOST")
@SequenceGenerator(name = "USEDBOOKPOST_SEQ_GEN", sequenceName = "USEDBOOKPOST_SEQ", allocationSize = 1)
public class UsedBookPost {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USEDBOOKPOST_SEQ_GEN")
    private Integer id;
    
    @Column(nullable = false)
    private Integer usedBookId;

    @Column
    private String content;
    
    @Builder.Default
    private Integer storage = 0; // 0 => 임시저장, 1 => 저장
    
    
    public UsedBookPost update(String content, Integer storage) {
        this.content = content;
        this.storage = storage;
        return this;
    }
    
}
