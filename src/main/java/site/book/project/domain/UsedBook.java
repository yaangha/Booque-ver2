package site.book.project.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.springframework.data.annotation.CreatedDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import site.book.project.dto.MarketCreateDto;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Entity(name = "USEDBOOK")
@SequenceGenerator(name = "USEDBOOK_SEQ_GEN", sequenceName = "USEDBOOK_SEQ", allocationSize = 1)
public class UsedBook extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USEDBOOK_SEQ_GEN")
    private Integer id;
    
    @Column(nullable = false)
    private Integer userId;
    
    @Column(nullable = false)
    private Integer bookId;
    
    @Column
    private String bookTitle;
    
    @Column
    private Integer price;
    
    // 판매 상태(판매중, 예약중, 판매완료)
    @Builder.Default
    private String status = "판매중";
    
    @Column
    private String location;
    
    @Column
    private String bookLevel;
    
    @Column
    private String title;
    
    @Builder.Default
    private Integer wishCount = 0;
    
    @Builder.Default
    private Integer hits = 0;
    
    @Builder.Default
    private Integer chats = 0;
    
    
    public UsedBook update(MarketCreateDto dto) {
    	this.bookTitle = dto.getBookTitle();
    	this.bookLevel = dto.getLevel();
    	this.price = dto.getPrice();
    	this.location = dto.getLocation();
    	this.title = dto.getTitle();
 
    	
    	return this;
    }

    // (하은) 책 판매여부 변경
    public UsedBook updateStauts(String status) {
        this.status = status;
        
        return this;
    }
    
    public UsedBook updateWishCount(Integer count) {
        this.wishCount = count;
        
        return this;
    }
    
    // (하은) 조회수 증가
    public UsedBook updateHits() {
        this.hits++;
        
        return this;
    }

        
}