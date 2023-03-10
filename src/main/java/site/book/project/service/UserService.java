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
        
        // ????????? ??????????????? ???????????? ??? DB??? insert
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

    // (??????) ????????? ??????
    public void update(Integer point, Integer id) {
        User user = userRepository.findById(id).get();
        user.update(point + user.getPoint());
        userRepository.save(user);
    }




    @Transactional
    public Integer modify(UserModifyDto userModifyDto, UserSecurityDto u) {
        // ????????????
        User user = userRepository.findById(u.getId()).get();
        user.updateProfile(userModifyDto);
        
        return 0;
    }

    
    @Value("${site.book.upload.path}") // (??????) ?????? ??????(?????? ??????) ??? ??????
    private String imageFilePath;
    
    // (??????) ????????? ????????? ?????????
    public void write(Integer id, MultipartFile file) throws IllegalStateException, IOException {
        log.info("imageFilePath!@!#%@={}",imageFilePath);
        UUID uuid = UUID.randomUUID();  // ?????????
        
        String fileName = uuid + "_" + file.getOriginalFilename();
        File saveFile=new File(imageFilePath, fileName); // saveFile: ?????? ?????????(??????) ???????????? ??????+???????????? ??????
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
    
    // (??????) ??? ????????? ????????? ??????????????? ???????????? - ?????? ?????? ?????? ??? ??? ??????????????????
    public Integer countMarket(Integer id) {
        List<UsedBook> usedBook = usedBookRepository.findByBookId(id); // ??????????????? ?????? ?????? ??? ?????? ??????(+????????????)
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
