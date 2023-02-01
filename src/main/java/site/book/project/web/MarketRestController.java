package site.book.project.web;


import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.UsedBook;
import site.book.project.dto.UsedBookStatus;
import site.book.project.dto.UserSecurityDto;
import site.book.project.repository.UsedBookRepository;
import site.book.project.service.UsedBookService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/market")
public class MarketRestController {

    private final UsedBookService usedBookService;
    private final UsedBookRepository usedBookRepository;
    
    
    @GetMapping("/api/usedBookWish")
    public Map<String, Object> saveUsedBookWish(Integer usedBookId, @AuthenticationPrincipal UserSecurityDto userSecurityDto) {
        
        log.info("하은 확인용 = {}", usedBookId);
        Integer result =usedBookService.addUsedBookWish(usedBookId, userSecurityDto.getId());
        Integer count = 0;
        if(result==0) {
            count= usedBookService.minusWishCount(usedBookId);
        }else {
            count = usedBookService.addWishCount(usedBookId);
            
        }
        
        Map<String, Object> map = new HashMap<>();
         
        map.put("check", result);
        map.put("count", count);
        return map;
    }
    
    
    @PostMapping("/changeStatus")
    public void changeStatus(@RequestBody UsedBookStatus status) {
        log.info("상태 변경을 ajax로 할거임 ,,{}",status.getStatus());
        log.info("상태 변경을 ajax로 할거임 ,,{}",status.getUsedBookId());
        Integer usedBookId = status.getUsedBookId();
        String selectStatus = status.getStatus();
        
        UsedBook usedBook = usedBookRepository.findById(usedBookId).get();
        usedBook = usedBook.updateStauts(selectStatus);
        usedBookRepository.save(usedBook);
        
    }
    
    
    
    
}
