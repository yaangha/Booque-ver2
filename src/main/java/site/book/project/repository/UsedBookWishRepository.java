package site.book.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.book.project.domain.UsedBookWish;

public interface UsedBookWishRepository extends JpaRepository<UsedBookWish, Integer> {

    UsedBookWish findByUserIdAndUsedBookId(Integer userId, Integer usedBookId);
    List<UsedBookWish> findByUserId(Integer userId);
//    
//    @Query("select u.usedBookId from USEDBOOKWISH u where userId = :userId")
//    List<Integer> selectUsedBookIdfromUserId(@Param(value="userId") Integer userId);

}
