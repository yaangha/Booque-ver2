package site.book.project.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MarketStorageDto {
    private Integer userId;
    private Integer bookId;
    private Integer usedBookId;
    private Integer price;
    private Integer location;
    private String level;
    private String title;
    private String content;
    private LocalDateTime modifiedTime;
    
}
