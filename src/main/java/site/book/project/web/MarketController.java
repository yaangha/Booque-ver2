package site.book.project.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Book;
import site.book.project.domain.UsedBook;
import site.book.project.domain.UsedBookPost;
import site.book.project.domain.User;
import site.book.project.dto.MarketCreateDto;
import site.book.project.dto.UserSecurityDto;
import site.book.project.repository.BookRepository;
import site.book.project.repository.SearchRepository;
import site.book.project.repository.UsedBookPostRepository;
import site.book.project.repository.UsedBookRepository;
import site.book.project.repository.UserRepository;
import site.book.project.service.SearchService;
import site.book.project.service.UsedBookService;

@Slf4j
@Controller
@RequestMapping("/market")
@RequiredArgsConstructor
public class MarketController {
    
    private final SearchRepository searchRepository;
    private final UsedBookService usedBookService;
    private final BookRepository bookRepository;
    private final UsedBookRepository usedBookRepository;
    private final UsedBookPostRepository usedBookPostRepository;
    private final UserRepository userRepository;
    
    
    @GetMapping("/main") // /market/main 부끄마켓 메인 페이지 이동
    public void main() {
        
    }
    
//    @GetMapping("/create") // /market/create 중고판매글 작성 페이지 이동
//    public void create(@AuthenticationPrincipal UserSecurityDto u , Integer bookId, Model model) {
//    	log.info("책 번호 {}", bookId);
//    	Book book = bookRepository.findById(bookId).get();
//    	
//    	Integer usedBookId = usedBookService.create(bookId, u.getId());
//    	log.info("책 검색을 하고 버튼을 누르면 디비단에 저장이 되게 해야해. {}, {}, {}", bookId, u.getId(), usedBookId);
//    	model.addAttribute("book", book);
//    	model.addAttribute("usedBookId", usedBookId);
//    }
    @GetMapping("/create") // /market/create 중고판매글 작성 페이지 이동
    public void create(Model model) {

    }
    
    @PostMapping("/create")
    public String createPost( @AuthenticationPrincipal UserSecurityDto userDto, MarketCreateDto dto,
    							Integer usedBookId) {
    	// 총 세개의 테이블을 크리에이트 해야함
    	
    	dto.setUserId(userDto.getId());
    	
    	usedBookService.create( usedBookId, dto );
    	
    	return "redirect:/market/detail?usedBookId="+usedBookId;
    }
    
    

    
    
    
    
    @GetMapping("/detail") // /market/detail 중고판매글 상세 페이지 이동
    public void detail(Integer usedBookId, Model model) {
        // 책 정보 불러오기(bookId) -> postId로 bookId 찾기
        // 판매글 정보 불러오기
        // 판매글제목 & 가격 & 수정시간 & 지역 & 본문 & 판매여부 & 책상태 & 이미지
        UsedBook usedBook = usedBookRepository.findById(usedBookId).get();        
        UsedBookPost usedBookPost = usedBookPostRepository.findByUsedBookId(usedBookId);
        User user = userRepository.findById(usedBook.getUserId()).get(); // 작성자의 정보
        Book book = bookRepository.findById(usedBook.getBookId()).get();
        
        // 판매하는 책과 동일한 책(다른 중고책) 리스트
        List<UsedBook> otherUsedBookList = usedBookService.readOtherUsedBook(usedBook.getBookId());
        List<UsedBook> otherUsedBookListFinal = new ArrayList<>();
        
        for (UsedBook u : otherUsedBookList) {
            if(usedBookId != u.getId()) {
                otherUsedBookListFinal.add(u);
            }
        }
        
        
        model.addAttribute("book", book);
        model.addAttribute("user", user); // userName만 보낼 수 있게 수정(?)
        model.addAttribute("usedBookPost", usedBookPost);
        model.addAttribute("usedBook", usedBook);
        model.addAttribute("otherUsedBookListFinal", otherUsedBookListFinal);
            
    }
    
    
    
    /**
     * ajax를 이용해서 키워드 받고 검색하기
     * @param keyword 검색할 단어(isbn은 아직은 제외됨)
     * @return
     */
    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<List<Book>> bookList(String keyword){
        log.info("확인 해야지 키워드가 잘 넘어갔는지{}   ",keyword);
        
        List<Book> searhList = searchRepository.unifiedSearchByKeyword(keyword);
        return ResponseEntity.ok(searhList);
    }
    
    /**
     * UsedBook 테이블에 userId, bookId 먼저 저장하기
     * @param u user 정보
     * @param bookId  선택한 책의 PK
     * @return  Map타입을 통해 Book과 usedBookId(PK)를 넘김
     */
    @GetMapping("/createUsed")
    @ResponseBody
    public Map<String, Object>  createUsedBook(@AuthenticationPrincipal UserSecurityDto u, Integer bookId) {
        // 저장
        
        Map<String, Object> maps = new HashMap<>();
        
        Integer usedBookId = usedBookService.create(bookId, u.getId());
        Book book = bookRepository.findById(bookId).get();
        
        maps.put("book", book);
        maps.put("usedBookId", usedBookId);
       return maps; 
    }
    

    /**
     * 
     * @param id 마이페이지에 표시될 user 
     * @param model
     */
    @GetMapping("/mypage") // /market/mypage 판매글작성자&마이페이지 이동
    public void mypage(Integer id, Model model) {
        User user = userRepository.findById(id).get();
        
        model.addAttribute("user", user);
    }
    
    @GetMapping("/modify")
    public void modify(Integer usedBookId) {
        
    }
    
    @PostMapping("/modifyStatus")
    public String modifyStatus(Integer statusUsedBookId, String selectStatus) {
        UsedBook usedBook = usedBookRepository.findById(statusUsedBookId).get();
        usedBook = usedBook.updateStauts(selectStatus);
        usedBookRepository.save(usedBook);
        
        log.info("하은 책 판매여부 확인 = {}", selectStatus);
        
        return "redirect:/market/detail?usedBookId=" + statusUsedBookId;
    }
}
