package site.book.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import site.book.project.domain.Chat;

public interface ChatRepository extends JpaRepository<Chat, Integer> {

    // 중고판매글 id, 구매자(채팅 처음 건 사람) id 넘겨 받아 2개가 동시에 일치하는 Chat방 찾기
    // (= 채팅방 중복 체크)
    // 중고판매글 id가 같으면 판매자 id도 같기 때문에, 판매자 id는 select문에 포함시키지 않음
    Chat findByUsedBookIdAndBuyerId(Integer usedBookId, Integer buyerId);
    
    Chat findByChatRoomId(Integer chatRoomId);
    
    // 내가 (판매자 혹은 구매자로) 포함된 모든 채팅방 찾기, 최신업뎃시간순 정렬
    Chat findByBuyerIdOrSellerIdOrderByModifiedTimeDesc(Integer sellerId, Integer buyerId);
    
}
