package site.book.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import site.book.project.domain.UsedBook;
import site.book.project.domain.UsedBookPost;

@AllArgsConstructor
@Builder
@Data
@ToString
@NoArgsConstructor      
public class MarketCreateDto {

	private Integer userId;  // 이미저장이 되어 있는데 필요할까?
	private Integer bookId;
	private String bookTitle;
	private Integer price;
	private String location;
	private String level;
	private String title;
	
	private String contents;  // 따로 저장되어야함. 
	

	
}
