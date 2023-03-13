# 📚Booque ver2

## 개요
**일정** 2023년 1월 11 ~ 2023년 2월 16일<br>
**인원** 6인 팀 프로젝트

## 사용 기술 및 개발환경
+ Java
+ Spring Boot
+ HTML
+ CSS
+ JavaScript

## 구현 기능
1. 임시저장


2. 다른 중고 판매글 노출

detail.html 일부

```html
<div id="other" th:unless="${ #lists.isEmpty(otherUsedBookListFinal2) }" style="border-top: 1px solid silver;"> <!-- 이 책의 다른 중고 상품은? -->
  <div style="font-size:18px; padding-bottom: 15px;"><span th:text="${ '<' + book.bookName + '>' }" style="font-size:21px; font-weight: bold;"></span> 다른 중고 상품은?</div>
  <div class="otherList align-middle" th:each="marketCreateDto : ${ otherUsedBookListFinal2 }"> <!-- 다른 상품 나열 부분 -->
    <div style="width:200px; height: 315px;"> <!-- 상품목록 1개 -->
    <a th:href="@{ /market/detail(usedBookId=${ marketCreateDto.usedBookId }) }" th:myname="${ marketCreateDto.usedBookId }" onclick="usedBookHits(this.getAttribute('myname'))">
            <img id="img" alt="" th:src="${ '/market/api/view/'+marketCreateDto.imgUsed }"  style="height:200px; width:200px; margin-bottom: 5px; object-fit: cover;">
      <div class="text-truncate" th:text="${ marketCreateDto.title }" style="font-size:17px;"></div>
      <div th:text="|${#numbers.formatInteger(marketCreateDto.price, 0, 'COMMA')}원|" style="font-weight: bold;"></div>
      <span th:text="${ marketCreateDto.level }" style="color:red; font-weight:bold;"></span>
            <small class="text-truncate" th:text="${ marketCreateDto.location }" style="display: block;"></small>
            <div class="align-middle" style="color:gray;">
                <small>
                <i class="bi bi-heart-fill" style="font-size:10px;"></i> 관심 <span id="wishCount" th:text="${ marketCreateDto.wishCount }"></span> 
                <i class="bi bi-eye" style="font-size:13px; margin-left:8px;"></i> 조회 <span th:text="${ marketCreateDto.hits }"></span>
            </small>
            </div>
    </a>
    </div>
  </div>
</div>
```

MarketController.java 일부

```java
// (하은) 같은 책 다른 중고상품 수정
List<UsedBook> otherUsedBookList = usedBookService.readOtherUsedBook(usedBook.getBookId());
List<MarketCreateDto> otherUsedBookList2 = mainList(otherUsedBookList);
List<MarketCreateDto> otherUsedBookListFinal2 = new ArrayList<>();

for (MarketCreateDto m : otherUsedBookList2) {
    if(usedBookId != m.getUsedBookId()) {
        otherUsedBookListFinal2.add(m);
    }
}

model.addAttribute("otherUsedBookListFinal2", otherUsedBookListFinal2);
```

UsedBookService.java 일부

```java
// (하은) bookId가 동일한 다른 중고책 리스트 만들기
public List<UsedBook> readOtherUsedBook(Integer bookId) {
    log.info("하은 중고책의 책 정보를 가진 아이디는? = {}", bookId);

    // (1) 같은 책의 중고판매글 리스트
    List<UsedBook> otherUsedBookListAll = usedBookRepository.findByBookId(bookId); 

    // (2) 임시저장 글 제외한 리스트 재생성
    List<UsedBook> otherUsedBookList = new ArrayList<>();

    for (UsedBook u : otherUsedBookListAll) {
        UsedBookPost storageChk = usedBookPostRepository.findByUsedBookId(u.getId());
        if (storageChk.getStorage() != 0) {
            otherUsedBookList.add(u);
        }
    }

    return otherUsedBookList;
}
```

3. 블로그 이동

detail.html 일부

```html
<div class="row bg-dark p-2 text-dark bg-opacity-10 rounded-4" style="height: 100px; margin: 15px 0; overflow: hidden; padding-right: none;">
    <div class="col-1" style="margin: auto;">
        <i class="bi bi-bookmarks-fill" style="font-size: 25px; margin: 30px;"></i>
    </div>
    <div class="col" style="margin: auto;"><!-- 블로그 목록 1개(임시) -->
        <div th:if="${ thisBookPost == null }"><!-- 해당 책 리뷰가 없을시 최신 리뷰 보여주기 -->
            <div th:if="${ latestPost == null }" style="margin: auto; font-size: 18px; font-weight: bold;"><span th:text="${ '[' + user.username + ']' }"></span>님이 작성하신 리뷰가 아직 없어요!</div>
            <div th:unless="${ latestPost == null }">
                <div style="color: gray; font-size: small;"><span th:text="${ user.username }"></span>님의 최신 리뷰 ↓</div>
                <a th:href="@{ /post/detail(postId=${ latestPost.postId }, bookId=${ book.bookId }, username=${ user.username }) }">
                <div class="text-truncate" style="font-weight: bold; font-size: 21px;" th:text="${ latestPost.title }"></div>
                <div style="font-size: small; color: gray;">
                    <i class="bi bi-clock"></i><span th:text="${ ' ' + #temporals.format(latestPost.modifiedTime , 'yyyy/MM/dd HH:mm') }"></span>
                </div>
                </a>
            </div>
        </div>
        <div th:unless="${ thisBookPost == null }"><!-- 해당 리뷰가 있을시 보여주기 -->
            <div style="color: gray; font-size: small;"><span th:text="${ user.username }"></span>님의 <span th:text="${ '<' + book.bookName + '>' }" style="font-weight: bold;"></span> 리뷰 ↓</div>
            <a th:href="@{ /post/detail(postId=${ thisBookPost.postId }, bookId=${ book.bookId }, username=${ user.username }) }">
            <div class="text-truncate" style="font-weight: bold; font-size: 21px;" th:text="${ thisBookPost.title }"></div>
            <div style="font-size: small; color: gray;">
                <i class="bi bi-clock"></i><span th:text="${ ' ' + #temporals.format(thisBookPost.modifiedTime , 'yyyy/MM/dd HH:mm') }"></span>
            </div>
            </a>
        </div>
    </div>
    <div class="col-4" style="margin: auto; text-align: right;">
        <a th:href="@{ /post/list(postWriter=${ user.username }) }" class="btn btn-dark" style="border: 1px solid black; padding: 16px 18px; margin:16px;"><span th:text="${ '[' + user.nickName + '] 님의 블로그 구경하기' }"></span>
        <i class="bi bi-house-door"></i></a>
    </div>
</a>
</div>
```


MarketController.java 일부

```java
// (하은) 블로그로 연결 -> 해당 책에 관한 리뷰 + 최신 리뷰 = 총 2개 보여주기
List<Post> userPostList = postRepository.findByUserIdOrderByCreatedTime(user.getId());

Post thisBookPost = null;
Post latestPost = null;

if (userPostList != null) {
    for (Post p : userPostList) {
        if (p.getBook().getBookId() == book.getBookId()) {
            thisBookPost = p;
            break;
        }
    }

    for (Post p : userPostList) {
        if (p.getBook().getBookId() != book.getBookId()) {
            latestPost = p;
            break;
        }
    }
} else {
    thisBookPost = null;
    latestPost = null;
}

model.addAttribute("thisBookPost", thisBookPost);
model.addAttribute("latestPost", latestPost);
```

4. 마이페이지

## 구성 화면
### 메인 페이지
![main](https://user-images.githubusercontent.com/113163657/224773967-ecef716c-1824-435c-9966-b6b6db222584.JPG)

---
### 검색 페이지
![search(1)](https://user-images.githubusercontent.com/113163657/224774242-64938848-2a3d-40da-8f8a-764b095cb231.JPG)

---
### 판매글 상세 페이지

---
### 마이페이지
![mypage(1)](https://user-images.githubusercontent.com/113163657/224774447-1e4fae3e-e4cf-4b0a-b08c-d1b7352bbdd2.JPG)
![mypage(2)](https://user-images.githubusercontent.com/113163657/224774501-9c5366c3-75c5-48af-a927-66955c51688c.JPG)
