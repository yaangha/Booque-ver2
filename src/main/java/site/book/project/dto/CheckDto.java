package site.book.project.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor 
public class CheckDto {

    private Integer usedBookId;
    private String title;
    private String content;
}
