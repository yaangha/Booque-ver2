package site.book.project.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Book;
import site.book.project.domain.Post;
import site.book.project.domain.UsedBook;
import site.book.project.domain.UsedBookImage;
import site.book.project.domain.UsedBookPost;
import site.book.project.domain.UsedBookWish;
import site.book.project.domain.User;
import site.book.project.dto.MarketCreateDto;
import site.book.project.dto.UserSecurityDto;
import site.book.project.repository.BookRepository;
import site.book.project.repository.PostRepository;
import site.book.project.repository.SearchRepository;
import site.book.project.repository.UsedBookImageRepository;
import site.book.project.repository.UsedBookPostRepository;
import site.book.project.repository.UsedBookRepository;
import site.book.project.repository.UsedBookWishRepository;
import site.book.project.repository.UserRepository;
import site.book.project.service.PostService;
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
    private final UsedBookWishRepository usedBookWishRepository;
    private final UsedBookImageRepository usedBookImageRepository;
    private final PostService postService;
    private final PostRepository postRepository;
    
    
    
    @GetMapping("/main") // /market/main 부끄마켓 메인 페이지 이동
    public void main(@AuthenticationPrincipal UserSecurityDto userDto,String orderSlt,   Model model) {
//        List<UsedBook> usedBookList = usedBookRepository.findByOrderByModifiedTimeDesc();
        
        List<UsedBook> usedBookList = new ArrayList<>(); // 메인에 넘길 리스트
        List<UsedBookPost> usedBookPost = new ArrayList<>(); // 임시저장된 리스트
        
        // 서비스로 넘겨야 할까? 
        if(orderSlt==null || orderSlt.equals("최신순")) {
            List<UsedBook> storageChk = usedBookRepository.findByOrderByCreatedTimeDesc();            
            for (UsedBook u : storageChk) {
                UsedBookPost post = usedBookPostRepository.findByUsedBookId(u.getId());
                if (post.getStorage() == 1) {
                    usedBookList.add(u);
                } else {
                    usedBookPost.add(post);                    
                }
            }
        }else if(orderSlt.equals("인기순")) {
            List<UsedBook> storageChk = usedBookRepository.findByOrderByHitsDesc();
            for (UsedBook u : storageChk) {
                UsedBookPost post = usedBookPostRepository.findByUsedBookId(u.getId());
                if (post.getStorage() == 1) {
                    usedBookList.add(u);
                } else {
                    usedBookPost.add(post);
                }
            }
        }
        
        List<MarketCreateDto> list = mainList(usedBookList);

        if(userDto != null) {
            model.addAttribute("userNickname", userDto.getNickName());       
        }
        
        
        model.addAttribute("list", list);
        model.addAttribute("orderSlt", orderSlt);
        model.addAttribute("usedBookPost", usedBookPost);
        
    }

    @GetMapping("/create") // /market/create 중고판매글 작성 페이지 이동
    public void create(@AuthenticationPrincipal UserSecurityDto userDto, Model model) {
       
    }
    
    @PostMapping("/create")
    public String createPost( @AuthenticationPrincipal UserSecurityDto userDto, MarketCreateDto dto,
    							Integer usedBookId) {
    	// 총 세개의 테이블을 크리에이트 해야함
        
        if(dto.getFileNames() != null) {
            usedBookService.createImg(usedBookId, dto.getFileNames());
            
        }
        
    	dto.setUserId(userDto.getId());
    	dto.setStorage(1); // storage 값을 1(저장)로 변경 => 디폴트 값은 0(임시저장)
    	usedBookService.create( usedBookId, dto );
    	
    	return "redirect:/market/detail?usedBookId="+usedBookId;
    }
    
    @GetMapping("/storage") // 메인화면 -> 상품등록에서 작성하던 글 이어서 작성하기 버튼 눌렀을 때!
    public void storage(@AuthenticationPrincipal UserSecurityDto userDto, Model model) {
        // (1) 사용자 글에서 임시저장 목록 뽑기 -> userid로 작성한 글 리스트업(내림차순) -> [0]번째 글 저장 -> marketcreatedto 사용해서 데이터 넘기기?
        List<UsedBook> usedBookList = usedBookRepository.findByUserIdOrderByModifiedTimeDesc(userDto.getId());
        
        List<UsedBookPost> usedBookPost = new ArrayList<>(); // storage가 0인 목록을 저장할 리스트
        
        for (UsedBook u : usedBookList) { // pk로 UsedBookPost에서 0인 목록 찾기 -> 먼저 나오는 값이 최신 순
            UsedBookPost post = usedBookPostRepository.findByUsedBookId(u.getId());
            if (post.getStorage() == 0) {
                usedBookPost.add(post);
            }
        }
        
        // usedBookPost[0]가 제일 최신순
        UsedBook usedBook = usedBookRepository.findById(usedBookPost.get(0).getUsedBookId()).get();
        Book book = bookRepository.findById(usedBook.getBookId()).get();
        MarketCreateDto dto = MarketCreateDto.builder()
                .usedBookId(usedBook.getId()).bookTitle(book.getBookName()).price(usedBook.getPrice()).location(usedBook.getLocation())
                .level(usedBook.getBookLevel()).title(usedBook.getTitle()).contents(usedBookPost.get(0).getContent())
                .build();
        
        List<UsedBookImage> imgList = usedBookImageRepository.findByUsedBookId(usedBook.getId());
        
        model.addAttribute("imgList", imgList);
        
        model.addAttribute("dto", dto);    
        model.addAttribute("book", book);
        model.addAttribute("usedBook", usedBook);
    }
    
    @PostMapping("/storage") // 임시저장 완료 후 부끄마켓 메인 페이지로 이동
    public String storage(@AuthenticationPrincipal UserSecurityDto userDto, MarketCreateDto dto, Integer usedBookId) {
        
        if(dto.getFileNames()!= null) {
            usedBookService.createImg(usedBookId, dto.getFileNames());
            
        }
        
        log.info("안들어가니?? 왜 ?? {}", dto.getFileNames());
        dto.setUserId(userDto.getId());
        dto.setStorage(0);
        usedBookService.create(usedBookId, dto);
        
        return "redirect:/market/main";
    }
        
    @GetMapping("/detail") // /market/detail 중고판매글 상세 페이지 이동
    public void detail(@AuthenticationPrincipal UserSecurityDto userDto ,Integer usedBookId, Model model) {
        // 책 정보 불러오기(bookId) -> postId로 bookId 찾기
        // 판매글 정보 불러오기
        // 판매글제목 & 가격 & 수정시간 & 지역 & 본문 & 판매여부 & 책상태 & 이미지
        UsedBook usedBook = usedBookRepository.findById(usedBookId).get();        
        UsedBookPost usedBookPost = usedBookPostRepository.findByUsedBookId(usedBookId);
        User user = userRepository.findById(usedBook.getUserId()).get(); // 작성자의 정보
        Book book = bookRepository.findById(usedBook.getBookId()).get();
        
        double bookPrice = book.getPrices();
        double usedPrice = usedBook.getPrice();
        
        double sale =  (1-usedPrice/bookPrice)*100;
        
        UsedBookWish wish = null;
        // 로그인 한 사람의 정보를 통해 내것도 하트 누를 수 있음!
        // userId, usedBookId가 있으니
        if(userDto != null) {
            wish = usedBookWishRepository.findByUserIdAndUsedBookId(userDto.getId(), usedBookId);
        }

        // (하은) 이미지 넘기기 -> 메인 1개 + 나머지 리스트
        List<UsedBookImage> imgListAll = usedBookImageRepository.findByUsedBookId(usedBookId);
        UsedBookImage firstImg = imgListAll.get(0); // 메인(처음에 보여질 이미지)
        // imgList.remove(0);
        
        List<UsedBookImage> imgList = new ArrayList<>(); // 메인을 제외한 나머지 이미지 리스트
        for (int i = 1; i < imgListAll.size(); i++) {
            imgList.add(imgListAll.get(i));
        }
        
        // (하은) 같은 책 다른 중고상품 수정
        List<UsedBook> otherUsedBookList = usedBookService.readOtherUsedBook(usedBook.getBookId());
        List<MarketCreateDto> otherUsedBookList2 = mainList(otherUsedBookList);
        List<MarketCreateDto> otherUsedBookListFinal2 = new ArrayList<>();

        for (MarketCreateDto m : otherUsedBookList2) {
            if(usedBookId != m.getUsedBookId()) {
                otherUsedBookListFinal2.add(m);
            }
        }
        
        // (하은) 블로그로 연결 -> 해당 책에 관한 리뷰 + 최신 리뷰 = 총 2개 보여주기
        List<Post> userPostList = postRepository.findByUserIdOrderByCreatedTime(user.getId()); // 작성자 블로그 글
        
        Post thisBookPost = null;
        Post latestPost = null;
        
        if (userPostList != null) {
            for (Post p : userPostList) {
                if (p.getBook().getBookId() == book.getBookId()) {
                    thisBookPost = p;
                    log.info("하은 블로그 연동 1 = {}", thisBookPost);
                    break;
                }
            }
            
            for (Post p : userPostList) {
                if (p.getBook().getBookId() != book.getBookId()) {
                    latestPost = p;
                    log.info("하은 블로그 연동 2 = {}", latestPost);
                    break;
                }
            }
        } else {
            thisBookPost = null;
            latestPost = null;
            log.info("하은 블로그 연동 3 = {}, {}", thisBookPost, latestPost);
        }
        
        model.addAttribute("thisBookPost", thisBookPost);
        model.addAttribute("latestPost", latestPost);
        model.addAttribute("firstImg", firstImg);
        model.addAttribute("imgList", imgList);
        model.addAttribute("sale", sale);
        model.addAttribute("wish", wish);
        model.addAttribute("book", book);
        model.addAttribute("user", user); 
        model.addAttribute("usedBookPost", usedBookPost);
        model.addAttribute("usedBook", usedBook);
        model.addAttribute("otherUsedBookListFinal2", otherUsedBookListFinal2);
        
    }
    
    // (하은) 조회수 증가
    @GetMapping("/usedBookHitCount")
        public void usedBookHitCount(Integer usedBookId, HttpServletRequest request, HttpServletResponse response) {
            usedBookService.updateHits(usedBookId, request, response);
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
    public void mypage(String userNickname,Model model) {
        
        model.addAttribute("user", userNickname);
        
    }
    
    @GetMapping("/modify")
    public void modify(Integer usedBookId, Model model) {
        UsedBook usedBook = usedBookRepository.findById(usedBookId).get();
        UsedBookPost usedBookPost = usedBookPostRepository.findByUsedBookId(usedBookId);   
        Book book = bookRepository.findById(usedBook.getBookId()).get();
        User user = userRepository.findById(usedBook.getUserId()).get();
        

        List<UsedBookImage> imgList = usedBookImageRepository.findByUsedBookId(usedBookId);
        
        model.addAttribute("imgList", imgList);
        model.addAttribute("usedBook", usedBook);
        model.addAttribute("usedBookPost", usedBookPost);
        model.addAttribute("book", book);
        model.addAttribute("user", user);
    }
    

    
    
    @PostMapping("/modify")
    public String modify(MarketCreateDto dto, String originLocation) {
        log.info("수정창에서 읽어오는 dto , {}", dto);
        log.info("주소 값을 안줄때는 원래 값을 읽어야 해! {}", originLocation);
        log.info( "{}",dto.getFileNames());
        
        if(dto.getFileNames() != null) {
            usedBookService.createImg(dto.getUsedBookId(), dto.getFileNames());
            
        }
        
        
        dto.setStorage(1);
        
        // 책 제목 null이 됨.. 
        if(dto.getLocation().equals("")) {
            dto.setLocation(originLocation);
        }
        usedBookService.create(dto.getUsedBookId(), dto);
        
        return "redirect:/market/detail?usedBookId=" + dto.getUsedBookId();
    }
    
    
    @GetMapping("/mainSearch")
    public void mainSearch(@AuthenticationPrincipal UserSecurityDto userDto ,String region, String mainKeyword, Model model,
                            String orderSlt , String status) {
        
        
        List<UsedBook> takeList = usedBookService.searchBookList(region, mainKeyword, orderSlt, status);
        
        List<MarketCreateDto> list = mainList(takeList);
        
        if(userDto != null) {
            model.addAttribute("userNickname", userDto.getNickName());       
        }
        
        model.addAttribute("status", status);
        model.addAttribute("orderSlt", orderSlt);
        model.addAttribute("list", list);
        model.addAttribute("region", region);
        model.addAttribute("mainKeyword", mainKeyword);
        
        
        
    }
    
    
    
    /**
     * main, 리스트 불러올때 사용함. 
     * @param usedBookList
     * @return
     */
    private List<MarketCreateDto> mainList(List<UsedBook> usedBookList) {
        
        List<MarketCreateDto> list = new ArrayList<>();
        
        for (UsedBook ub : usedBookList) {
                User user = userRepository.findById(ub.getUserId()).get();
                Book book = bookRepository.findById(ub.getBookId()).get();
                List<UsedBookImage> imgList = usedBookImageRepository.findByUsedBookId(ub.getId());
                
                MarketCreateDto dto = MarketCreateDto.builder()
                        .usedBookId(ub.getId())
                        .userId(user.getId()).username(user.getUsername())
                        .userImage(user.getUserImage()).nickName(user.getNickName())
                        .bookTitle(book.getBookName()).price(ub.getPrice())
                        .location(ub.getLocation()).level(ub.getBookLevel()).title(ub.getTitle()).modifiedTime(ub.getModifiedTime()).hits(ub.getHits()).wishCount(ub.getWishCount())
                       .imgUsed(imgList.get(0).getFileName())
                        .build();
                list.add(dto);
        
        }
        
        
        return list;
    }
    
    
}