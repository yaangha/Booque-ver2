# 📚Booque ver2

## 개요
**일정** 2023년 1월 11일 ~ 2023년 2월 16일<br>
**인원** 6인 팀 프로젝트

## 사용 기술 및 개발환경
+ Java
+ Spring Boot
+ HTML
+ CSS
+ JavaScript

## 구현 기능(담당)
+ 임시저장

> MarketController.java 일부

```java
@PreAuthorize("hasRole('USER')")
@GetMapping("/storage") // 메인화면 -> 상품등록에서 작성하던 글 이어서 작성하기 버튼 눌렀을 때!
public void storage(@AuthenticationPrincipal UserSecurityDto userDto, Model model) {
    // (1) 사용자 글에서 임시저장 목록 뽑기 -> userid로 작성한 글 리스트업(내림차순) -> [0]번째 글 저장 
    List<UsedBook> usedBookList = usedBookRepository.findByUserIdOrderByModifiedTimeDesc(userDto.getId());

    List<UsedBookPost> usedBookPost = new ArrayList<>(); // storage가 0인 목록을 저장할 리스트
    for (UsedBook u : usedBookList) { // pk로 UsedBookPost에서 0인 목록 찾기 -> 먼저 나오는 값이 최신 순
        UsedBookPost post = usedBookPostRepository.findByUsedBookId(u.getId());
        if (post.getStorage() == 0) {
            usedBookPost.add(post);
        }
    }

    // usedBookPost[0]가 제일 최신순
    UsedBook usedBook = usedBookRepository.findById(usedBookPost.get(0).getUsedBookId()).get();
    Book book = bookRepository.findById(usedBook.getBookId()).get();
    MarketCreateDto dto = MarketCreateDto.builder()
            .usedBookId(usedBook.getId()).bookTitle(book.getBookName()).price(usedBook.getPrice()).location(usedBook.getLocation())
            .level(usedBook.getBookLevel()).title(usedBook.getTitle()).contents(usedBookPost.get(0).getContent())
            .build();

    List<UsedBookImage> imgList = usedBookImageRepository.findByUsedBookId(usedBook.getId());

    model.addAttribute("imgList", imgList);
    model.addAttribute("dto", dto);    
    model.addAttribute("book", book);
    model.addAttribute("usedBook", usedBook);
}

@PostMapping("/storage") // 임시저장 완료 후 부끄마켓 메인 페이지로 이동
public String storage(@AuthenticationPrincipal UserSecurityDto userDto, MarketCreateDto dto, Integer usedBookId) {
    List<String> defaultImg = new ArrayList<>();
    defaultImg.add("booque_logo.jpg");

    if(dto.getFileNames()!= null) {
        usedBookService.createImg(usedBookId, dto.getFileNames());
    } else {
        usedBookService.createImg(usedBookId, defaultImg);
    }

    dto.setUserId(userDto.getId());
    dto.setStorage(0);
    usedBookService.create(usedBookId, dto);

    return "redirect:/market/main";
}
```

+ 다른 중고 판매글 노출

> detail.html 일부

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

> MarketController.java 일부

```java
// 같은 책 다른 중고상품 수정
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

> UsedBookService.java 일부

```java
// bookId가 동일한 다른 중고책 리스트 만들기
public List<UsedBook> readOtherUsedBook(Integer bookId) {
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

+ 블로그 이동

> detail.html 일부

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
</div>
```

> MarketController.java 일부

```java
// 블로그로 연결 -> 해당 책에 관한 리뷰 + 최신 리뷰 = 총 2개 보여주기
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

+ 마이페이지

> MarketController.java 일부

```java
@GetMapping("/mypage") // 
public void mypage(Integer userId, Model model) {
    User user = userRepository.findById(userId).get();
    List<UsedBook> usedBookAll = new ArrayList<>(); // 사용자에 따른 중고책 판매 리스트
    List<UsedBook> usedBook = new ArrayList<>(); // usedBookAll에서 임시저장 제외한 리스트(메인에 표시될 목록)
    usedBookAll = usedBookRepository.findByUserId(userId);

    for (UsedBook u : usedBookAll) {
        UsedBookPost usedBookPost = usedBookPostRepository.findByUsedBookId(u.getId());
        if (usedBookPost.getStorage() == 1) {
            usedBook.add(u);
        }
    }

    List<MarketCreateDto> list = mainList(usedBook); 
    List<UsedBookWish> wishList = usedBookWishRepository.findByUserId(userId); // 사용자가 찜한 리스트
    List<UsedBook> wishUsedBook = new ArrayList<>();

    for (UsedBookWish u : wishList) {
        UsedBook usedBookCHK = usedBookRepository.findById(u.getUsedBookId()).get();
        if (usedBookCHK.getId() == u.getUsedBookId()) {
            wishUsedBook.add(usedBookCHK);
        }
    }
    List<MarketCreateDto> wishListCHK = mainList(wishUsedBook); // 찜한 리스트 중 화면에 보여줄 목록

    // 리뷰 + 판매중 + 판매완료 개수
    Integer postCount = postRepository.findByUserId(userId).size();
    Integer usedBookSoldoutCount = usedBookRepository.countUsedBookSoldoutPost(userId, "판매완료").size();
    Integer countAllUsedBook = usedBook.size();
    Integer usedBookSellingCount = countAllUsedBook - usedBookSoldoutCount;

    model.addAttribute("wishListCHK", wishListCHK);
    model.addAttribute("user", user);
    model.addAttribute("list", list);
    model.addAttribute("usedBook", usedBook);
    model.addAttribute("postCount", postCount);
    model.addAttribute("usedBookSellingCount", usedBookSellingCount);
    model.addAttribute("usedBookSoldoutCount", usedBookSoldoutCount);
}
```

## 구성 화면
### 메인 페이지

+ 중고 거래 상품 목록을 확인할 수 있으며 상품을 등록할 수 있음  

![main](https://user-images.githubusercontent.com/113163657/224998838-724028b2-c65b-48e3-bb46-8bcd2ff38e8f.JPG)

---
### 검색 페이지

+ 지역, 키워드 등으로 검색할 수 있으며 알림 신청을 할 수 있음  

![search(1)](https://user-images.githubusercontent.com/113163657/224774242-64938848-2a3d-40da-8f8a-764b095cb231.JPG)

---
### 판매글 상세 페이지

+ 상품 이미지와 가격 등 중고 판매 정보를 확인할 수 있으며 판매자의 블로그로도 이동할 수 있음 

![detail](https://user-images.githubusercontent.com/113163657/224998940-7ee00de4-bf6b-4a56-891c-0890cef09cca.png)

+ 동일한 책의 다른 중고 상품 목록을 볼 수 있음  

![other](https://user-images.githubusercontent.com/113163657/224998949-1d54cab2-f4f4-44b3-bead-30b3fa82e5d4.png)

### 시즌1 새 책 판매 페이지

+ 새상품 상세 페이지에서, 중고 판매 상품이 있는 경우 해당 목록으로 이동할 수 있음  

![detail2](https://user-images.githubusercontent.com/113163657/224999321-571acce9-83a0-4713-b56f-f82704db3d04.png)

---
### 판매글 작성 페이지

+ 책을 검색하여 등록할 수 있으며 이미지 업로드도 가능함  

![create](https://user-images.githubusercontent.com/113163657/224999066-9b64f759-27ef-4b0f-85a8-3e6702778ce1.png)

+ 상품등록시 임시저장을 해놓은 글이 있다면 이어서 작성이 가능함  

![storage](https://user-images.githubusercontent.com/113163657/224999136-909c0b00-772a-4fef-b042-5f2ca49dea4d.png)

---
### 채팅 페이지

+ 중고 상품 판매자와 구매자가 채팅할 수 있으며 좌측에서 채팅 목록을 모아볼 수 있음  

![chat](https://user-images.githubusercontent.com/113163657/224999457-3ee6fa77-6583-4ebd-ab2e-228a8810deb2.JPG)

---
### 마이페이지

+ 블로그로 이동할 수 있으며, 판매 물품과 좋아요를 누른 목록을 볼 수 있음  

![mypage(1)](https://user-images.githubusercontent.com/113163657/224999168-5615de15-c9db-4115-8507-0337bfd306e2.JPG)
![mypage(2)](https://user-images.githubusercontent.com/113163657/224999178-9e4e40be-bb2c-4c50-bd4a-5d52ed645320.JPG)
