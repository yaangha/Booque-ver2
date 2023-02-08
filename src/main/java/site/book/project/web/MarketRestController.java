package site.book.project.web;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Book;
import site.book.project.domain.UsedBook;
import site.book.project.dto.FileUploadDto;
import site.book.project.dto.FileUploadResultDto;
import site.book.project.dto.UsedBookStatus;
import site.book.project.dto.UserSecurityDto;
import site.book.project.repository.SearchRepository;
import site.book.project.repository.UsedBookRepository;
import site.book.project.service.UsedBookService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/market")
public class MarketRestController {

    private final UsedBookService usedBookService;
    private final UsedBookRepository usedBookRepository;
    private final SearchRepository searchRepository;
    
    @Value("${com.example.upload.path}")
    private String uploadPath;
    
    
    @PostMapping("/api/upload")
    public ResponseEntity<List<FileUploadResultDto>> upload(FileUploadDto dto){
        
        
        List<MultipartFile> files = dto.getFiles();
        if (files == null) {
            return ResponseEntity.noContent().build();
        }
        
        List<FileUploadResultDto> list = new ArrayList<>();
        files.forEach(mutipartFile -> {
            FileUploadResultDto result = saveFile(mutipartFile);
            list.add(result);
        });
        
        return ResponseEntity.ok(list);
    }
    
    
    private FileUploadResultDto saveFile(MultipartFile file) {
        FileUploadResultDto result = null;
        
        String originalName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        boolean image = false;
        String target = uuid + "_" + originalName;
        log.info(target);
        
//        Path path = Paths.get(uploadPath, target);
        File dest = new File(uploadPath, target);
        try {
//            file.transferTo(path);
            file.transferTo(dest);
            
//            if (file.getContentType().startsWith("image")) {
//                image = true;
//                String thumbnailTarget = "s_" + target;
//                File thumbnailDest = new File(uploadPath, thumbnailTarget);
//                Thumbnailator.createThumbnail(dest, thumbnailDest, 200, 200);
//                
//            }
            
            result = FileUploadResultDto.builder()
                    .uuid(uuid)
                    .fileName(originalName)
                    .image(image)
                    .build();
            
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    @GetMapping("/api/view/{fileName}")
    public ResponseEntity<Resource> viewFile(@PathVariable String fileName) {
        log.info("viewFile(fileName={})", fileName);
        
        File file = new File(uploadPath, fileName);
        
        String contentType = null;
        try {
            contentType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            log.error("{} : {}", e.getCause(), e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", contentType);
        
        Resource resource = new FileSystemResource(file);
        
        return ResponseEntity.ok().headers(headers).body(resource);
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
    
    
    
    // (은정)
    @GetMapping("/api/usedBookWish")
    public Map<String, Object> saveUsedBookWish(Integer usedBookId, @AuthenticationPrincipal UserSecurityDto userSecurityDto) {
        
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
    
    //(은정)
    @PostMapping("/changeStatus")
    public void changeStatus(@RequestBody UsedBookStatus status) {
        Integer usedBookId = status.getUsedBookId();
        String selectStatus = status.getStatus();
        
        UsedBook usedBook = usedBookRepository.findById(usedBookId).get();
        usedBook = usedBook.updateStauts(selectStatus);
        usedBookRepository.save(usedBook);
        
    }
    
    
    
    
}
