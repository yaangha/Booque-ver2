package site.book.project.web;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.dto.UserSecurityDto;
import site.book.project.service.UsedBookService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/market")
public class MarketRestController {

    private final UsedBookService usedBookService;
    
    @GetMapping("/api/usedBookWish")
    public Integer saveUsedBookWish(Integer usedBookId, @AuthenticationPrincipal UserSecurityDto userSecurityDto) {
        
        log.info("하은 확인용 = {}", usedBookId);
        Integer result =usedBookService.addUsedBookWish(usedBookId, userSecurityDto.getId());
        
        if(result==0) {
            usedBookService.minusWishCount(usedBookId);
        }else {
            usedBookService.addWishCount(usedBookId);
            
        }
        
        
        return result;
    }
    
}
