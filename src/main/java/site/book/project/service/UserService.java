package site.book.project.service;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.UsedBook;
import site.book.project.domain.UsedBookPost;
import site.book.project.domain.User;
import site.book.project.dto.UserModifyDto;
import site.book.project.dto.UserRegisterDto;
import site.book.project.dto.UserSecurityDto;
import site.book.project.dto.UserSigninDto;
import site.book.project.repository.UsedBookPostRepository;
import site.book.project.repository.UsedBookRepository;
import site.book.project.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UsedBookRepository usedBookRepository;
    private final UsedBookPostRepository usedBookPostRepository;
    
    
    
    
    
    public String checkUsername(String username) {
        log.info("checkUsername(username = {})", username);
        
        Optional<User> result = userRepository.findByUsername(username);
        if (result.isPresent()) {
                return "namenok";
            } else {
                return "nameok"; 
            }
    }
    
    public String checkNickname(String nickname) {
        log.info("checkNickname(nickname = {})", nickname);
        
        Optional<User> result = userRepository.findByNickName(nickname);
        if (result.isPresent()) {
            return "nicknok";
        } else {
            return "nickok";
        }
    }

    public User registerUser(UserRegisterDto dto) {

        log.info("registerMember(dto = {})", dto);
        
        // 로그인 비밀번호를 암호화한 후 DB에 insert
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        User entity = userRepository.save(dto.toEntity());
        log.info("entity = {}", entity);
        
        return entity;
    }
    
    public User read(Integer userId) {
        return userRepository.findById(userId).get();
    }
    
    public User read(String username) {
        return userRepository.findByUsername(username).get();
    }

    public String checkEmail(String email) {

        Optional<User> result = userRepository.findByEmail(email);
        if (result.isPresent() ) {
            return "emailnok";
        } else {
            return "emailok";
        }
    }

    public String signIn(String username, String password) {
        log.info("checkPw userid = {} password = {}", username, password);
        User user = userRepository.findByUsername(username).get();
        log.info("checkPassword user = {}", user);
        String encodingPw = user.getPassword();
        log.info(encodingPw);
        Boolean confirm = confirm(password, encodingPw);
        log.info("confirm = {}", confirm);
        
        if (confirm == true) {
            return "ok";
        } else {
            return "nok";
        }
    }
    
    public User signinUser(UserSigninDto dto) {
        User user = userRepository.findByUsername(dto.getUsername()).get();
        log.info("find in signinUser user = {}", user);
        return user;
    }
    
    

    private Boolean confirm(String password, String password2) {
        return passwordEncoder.matches(password, password2);
    }
    
    public Optional<User> getUserBySigninId(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> read() {
       
        return userRepository.findAll();
    }

    // (하은) 포인트 적립
    public void update(Integer point, Integer id) {
        User user = userRepository.findById(id).get();
        user.update(point + user.getPoint());
        userRepository.save(user);
    }




    @Transactional
    public Integer modify(UserModifyDto userModifyDto, UserSecurityDto u) {
        // 중복검사
        User user = userRepository.findById(u.getId()).get();
        user.updateProfile(userModifyDto);
        
        return 0;
    }

    
    @Value("${site.book.upload.path}") // (예진) 절대 경로(외부 경로) 값 주입
    private String imageFilePath;
    
    // (예진) 프로필 이미지 업로드
    public void write(Integer id, MultipartFile file) throws IllegalStateException, IOException {
        log.info("imageFilePath!@!#%@={}",imageFilePath);
        UUID uuid = UUID.randomUUID();  // 식별자
        
        String fileName = uuid + "_" + file.getOriginalFilename();
        File saveFile=new File(imageFilePath, fileName); // saveFile: 파일 껍데기(객체) 생성해서 경로+파일이름 저장
        file.transferTo(saveFile);
        
        User user = userRepository.findById(id).get();
        
        user.setFileName(fileName);
        user.setFilePath(imageFilePath+"/"+fileName);
        user.setUserImage("/view/"+fileName);
        
        userRepository.save(user);
        log.info("fileName={}", fileName);
        log.info("filePath={}",imageFilePath+fileName);
        log.info("user.getUserImage ={}", user.getUserImage());
    }
    
    // (하은) 책 디테일 창에서 부끄장터로 연결하기 - 해당 책은 중고 몇 권 들어와있는지
    public Integer countMarket(Integer id) {
        List<UsedBook> usedBook = usedBookRepository.findByBookId(id); // 부끄마켓에 있는 해당 책 목록 전체(+임시저장)
        List<UsedBook> usedBookList = new ArrayList<>();
        
        for (UsedBook u : usedBook) {
            UsedBookPost usedBookPost = usedBookPostRepository.findByUsedBookId(u.getId());
            if (usedBookPost.getStorage() == 1) {
                usedBookList.add(u);
            }
        }
        
        return usedBookList.size();
    }
}
