package site.book.project.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
// 책 한줄평 REST 컨트롤러( AJax)
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Post;
import site.book.project.dto.BookCommentReadDto;
import site.book.project.dto.BookCommentRegisterDto;
import site.book.project.dto.PostReadDto;
import site.book.project.dto.UserSecurityDto;
import site.book.project.service.BookCommentService;
import site.book.project.service.PostService;

@Slf4j
@RequiredArgsConstructor
@RestController  // response.data
public class BookCommentRestController {
    // BookDetail 페이지에서 사용할 AJax  REST controller comment와 post 함께 사용함
 
    private final BookCommentService bookCommentService;
    private final PostService postService;
    
    @PostMapping("/api/comment") // 한줄평 insert
    public ResponseEntity<Integer> registerComment(@RequestBody BookCommentRegisterDto dto,
            @AuthenticationPrincipal UserSecurityDto userSecurityDto){
        log.info("한줄평 dto ={}",dto);
        Integer userId = userSecurityDto.getId();
        Integer commentId = bookCommentService.create(dto, userId);
        
        return ResponseEntity.ok(commentId);
    }
    
    @GetMapping("/api/comment/all/{bookId}")
    public ResponseEntity<List<BookCommentReadDto>> readAllCommentDesc(@PathVariable Integer bookId) {
        List<BookCommentReadDto> list = bookCommentService.readComment(bookId);
        
        return ResponseEntity.ok(list);
    }
    
    @GetMapping("/api/comment/allOrderLike/{bookId}")
    public ResponseEntity<List<BookCommentReadDto>> readAllComment(@PathVariable Integer bookId) {
        List<BookCommentReadDto> list = bookCommentService.readLikeComment(bookId);
        
        return ResponseEntity.ok(list);
        
    }
 
    @GetMapping("/api/post/all/{bookId}")
    public ResponseEntity<List<PostReadDto>> readPostDesc(@PathVariable Integer bookId){
        List<PostReadDto> list = postService.findDesc(bookId);
     
        return ResponseEntity.ok(list);
    }
    
    @GetMapping("/api/post/scoreDesc/{bookId}")
    public ResponseEntity<List<PostReadDto>> readPostScoreDesc(@PathVariable Integer bookId){
        List<PostReadDto> list = postService.findScoreDesc(bookId);
     
        return ResponseEntity.ok(list);
    }
    
    @GetMapping("/api/post/score/{bookId}")
    public ResponseEntity<List<PostReadDto>> readPostScore(@PathVariable Integer bookId){
        List<PostReadDto> list = postService.findScore(bookId);
     
        return ResponseEntity.ok(list);
    }
    
    // (하은)
    @GetMapping("/api/post/content/{postId}")
    public ResponseEntity<PostReadDto> readPostContent(@PathVariable Integer postId) {
        Post post = postService.read(postId);
        PostReadDto dto = PostReadDto.fromEntity(post);
        
        return ResponseEntity.ok(dto);
    }
}
