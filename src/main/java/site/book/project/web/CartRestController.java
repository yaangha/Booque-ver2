package site.book.project.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.dto.BuyInfoDto;
import site.book.project.dto.CartDto;
import site.book.project.dto.UserSecurityDto;
import site.book.project.service.CartService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CartRestController {

    private final CartService cartService;
    
    @GetMapping("api/cart/all/{userId}")
    public ResponseEntity<List<CartDto>> read(@PathVariable Integer userId){
        
        return ResponseEntity.ok(cartService.cartDtoList(userId));
    }

    @PostMapping("api/cartid")
    public ResponseEntity<List<CartDto>> cartListll(@RequestBody ArrayList<Integer> ckList){

        cartService.deleteCart(ckList);
        // 유저 번호 그냥 얻자...  가장 끝번호에 추가하면 되잖아
        // 유저 번호 
        List<CartDto> cartDtoList =  cartService.cartDtoList(ckList.get(ckList.size()-1));
        log.info("유저번호가 제대로 넘어 갔나 ? {} , {}",ckList.get(ckList.size()-1) , cartDtoList );
        
        return ResponseEntity.ok(cartDtoList);
    }

    /**
     * mapping 주소값은 넘어오는 parameter와 동일하지 않아도
     * @param count 장바구니 변경되는 수량
     * @param cartId 디비에 저장된 장바구니 고유값
     */
    @GetMapping("api/cartCount/{count}/{cartId}")
    public void updateCount(@PathVariable Integer count, @PathVariable Integer cartId){
        log.info("장바구니 수량 변경~~~~~~{},{}", count, cartId);
        cartService.cartCountUpdate(count, cartId);
        
    }
    

    @GetMapping("/cartTest/{test}")
    public void test(@PathVariable Integer test) {
        log.info("이거 맞아???? 뭐지..??    {}" ,test);
    }
    
    //(하은수정) 장바구니 버튼 누르면 데이터 저장 -> 모달창은 장바구니 이동여부만 체크
    @PostMapping("api/cartAdd")
    public void saveCart(@RequestBody BuyInfoDto dto, @AuthenticationPrincipal UserSecurityDto userSecurityDto) {
        Integer userId = userSecurityDto.getId(); // userId로 cart, wish 정보 찾기
        
        // 장바구니 데이터 cart table에 생성
        if (cartService.checkUser(userId, dto.getBookId()) == 1) { // 사용자 없으면 create
            cartService.addCart(userId, dto.getBookId(), dto.getCount());
        } else { // 사용자 있으면 update
            Integer afterCount = cartService.updateCount(userId, dto.getBookId(), dto.getCount());
            log.info("변경 수량={}", afterCount);
        }
    }
 
    
}
