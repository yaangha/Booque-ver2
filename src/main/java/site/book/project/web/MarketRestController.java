package site.book.project.web;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.dto.UserSecurityDto;
import site.book.project.service.UsedBookService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MarketRestController {

    private final UsedBookService usedBookService;
    
    // (하은) 코드 수정 필요!! 지금 실패함!! 찜하기 버튼 누르면 DB에 저장 
    @PostMapping("/api/usedBookWish")
    public void saveUsedBookWish(@PathVariable Integer usedBookId, @AuthenticationPrincipal UserSecurityDto userSecurityDto) {
        usedBookService.addUsedBookWish(usedBookId, userSecurityDto.getId());
        log.info("하은 확인용 = {}", usedBookId);
        
    }
    
}
