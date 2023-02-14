package site.book.project.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Data
@ToString
@NoArgsConstructor      
public class MarketCreateDto {

	private Integer userId;  // 이미저장이 되어 있는데 필요할까?
	private String username;
	private String nickName;
	private Integer bookId;
//	private Integer bookId;
    private Integer usedBookId;
	private String bookTitle;
	private Integer price;
	private String location;
	private String level;
	private String title;
	private String contents;  // 따로 저장되어야함. 
	private LocalDateTime modifiedTime;
	private Integer hits;
	private Integer wishCount;
	private String status;
	
	private List<String> fileNames;
	private Integer storage;
	//
	private String userImage;
	
	private String imgUsed;
}
