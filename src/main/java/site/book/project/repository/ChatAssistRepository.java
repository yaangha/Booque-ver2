package site.book.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import site.book.project.domain.ChatAssist;

public interface ChatAssistRepository extends JpaRepository<ChatAssist, Integer>  {

    ChatAssist findByChatRoomId(Integer chatRoomId);

}
