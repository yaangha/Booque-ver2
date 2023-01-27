package site.book.project.web;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Book;
import site.book.project.service.SearchService;

@Slf4j
@Controller
@RequestMapping("/market")
@RequiredArgsConstructor
public class MarketController {
    
    private final SearchService searchService;
    
    @GetMapping("/main") // /market/main 부끄마켓 메인 페이지 이동
    public void main() {
        
    }
    
    @GetMapping("/create") // /market/create 중고판매글 작성 페이지 이동
    public void create() {
        
    }
    @GetMapping("/search") // /market/create 중고판매글 작성 페이지 이동
    public void searchUsed() {

        
    }
    @GetMapping("/searchResult") // 검색후 페이지?
    public String search(String typeUsed, String keywordUsed, Model model, @PageableDefault(size=10) Pageable pageable) {
        log.info("검색 하는거 다시 만들자~!{}, {}",typeUsed, keywordUsed);
        
        Page<Book> searchList = searchService.search(typeUsed, keywordUsed, "0", pageable);
        for(Book s: searchList) {
            log.info("보자보자~!! {}", s);
            
        }
        log.info("보자보자~!! {}", searchList);

        
        return "/market/search";
    }
    
}
