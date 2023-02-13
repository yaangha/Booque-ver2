package site.book.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.book.project.domain.UsedBook;
import site.book.project.dto.MarketCreateDto;

public interface UsedBookRepository extends JpaRepository<UsedBook, Integer> {

    // (하은) bookId로 동일한 다른 중고책 리스트 찾기
    List<UsedBook> findByBookId(Integer bookId);
    Optional<UsedBook> findById(Integer userId);
    List<UsedBook> findByUserId(Integer userId);
    

    @Query("select u.usedBookId from USEDBOOKWISH u where userId = :userId")
    List<UsedBook> selectUsedBookIdfromUserId(@Param(value="userId") Integer userId);

    
//    @Query("select u from USEDBOOK u where u.userId = :userId and {u.status = '판매중' or u.status = '예약중'}")
//    List<UsedBook> countUsedBookSellingPost(@Param(value="userId")Integer userId);
    
    
    @Query("select u from USEDBOOK u where u.userId = :userId and u.status = :status")
    List<UsedBook> countUsedBookSoldoutPost(@Param(value="userId") Integer userId, @Param(value="status") String status);
    
    // (하은) 중고판매글 조회수 증감
    /*
    @Modifying // @Query로 작성된 변경, 삭제 쿼리 메서드 사용할 때 필용
    @Query("update UsedBook u set u.hits = u.hits + 1 where u.id = :id")
    int updateHits(Integer id); // id => UsedBook의 PK
    */
    
    
    //select * from usedbook where location  like '서울%' and (book_title like '%회복%' or title like '%회복%');
    // 검색을 책 제목과 글 제목으로만 함
    @Query(
            "select b from USEDBOOK b "
            + " where b.location like ( :region || '%') "
            + " and (b.bookTitle like ('%' || :keyword || '%') or b.title like ('%'|| :keyword || '%' ))"
            + " order by b.createdTime desc"
            )
    List<UsedBook> searchM(@Param(value = "region") String region,@Param(value = "keyword") String keyword );
    
    @Query(
            "select b from USEDBOOK b "
                    + " where b.location like ( :region || '%') "
                    + " and (b.bookTitle like ('%' || :keyword || '%') or b.title like ('%'|| :keyword || '%' ))"
                    + " order by b.price"
            )
    List<UsedBook> searchPrice(@Param(value = "region") String region,@Param(value = "keyword") String keyword );
    @Query(
            "select b from USEDBOOK b "
                    + " where b.location like ( :region || '%') "
                    + " and (b.bookTitle like ('%' || :keyword || '%') or b.title like ('%'|| :keyword || '%' ))"
                    + " order by b.price desc"
            )
    List<UsedBook> searchPriceDesc(@Param(value = "region") String region,@Param(value = "keyword") String keyword );
    
    /**
     * 판매중인 상품만 보여주기
     * @param region
     * @param keyword
     * @return
     */
    @Query(
            "select b from USEDBOOK b "
                    + " where b.location like ( :region || '%') "
                    + " and b.status = '판매중' "
                    + " and (b.bookTitle like ('%' || :keyword || '%') or b.title like ('%'|| :keyword || '%' ))"
                    + " order by b.createdTime desc"
            )
    List<UsedBook> searchM2(@Param(value = "region") String region,@Param(value = "keyword") String keyword );
    
    @Query(
            "select b from USEDBOOK b "
                    + " where b.location like ( :region || '%') "
                    + " and b.status = '판매중' "
                    + " and (b.bookTitle like ('%' || :keyword || '%') or b.title like ('%'|| :keyword || '%' ))"
                    + " order by b.price"
            )
    List<UsedBook> searchPrice2(@Param(value = "region") String region,@Param(value = "keyword") String keyword );
    @Query(
            "select b from USEDBOOK b "
                    + " where b.location like ( :region || '%') "
                    + " and b.status = '판매중' "
                    + " and (b.bookTitle like ('%' || :keyword || '%') or b.title like ('%'|| :keyword || '%' ))"
                    + " order by b.price desc"
            )
    List<UsedBook> searchPriceDesc2(@Param(value = "region") String region,@Param(value = "keyword") String keyword );
    
    
    List<UsedBook> findByOrderByCreatedTimeDesc();
    List<UsedBook> findByOrderByHitsDesc();

    // (하은) userId, storage로 임시저장 목록 찾기
    List<UsedBook> findByUserIdOrderByModifiedTimeDesc(Integer id);
    
}

