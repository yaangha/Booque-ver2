package site.book.project.dto;

import lombok.Data;

@Data
public class UsedBookReserveDto {
    
    // 예약 정보를 생성하기 위한 dto
    
    
    private Integer usedBookId;
    private Integer userId;

}
