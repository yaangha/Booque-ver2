# ๐Booque ver2

## ๊ฐ์
**์ผ์ ** 2023๋ 1์ 11 ~ 2023๋ 2์ 16์ผ<br>
**์ธ์** 6์ธ ํ ํ๋ก์ ํธ

## ์ฌ์ฉ ๊ธฐ์  ๋ฐ ๊ฐ๋ฐํ๊ฒฝ
+ Java
+ Spring Boot
+ HTML
+ CSS
+ JavaScript

## ๊ตฌํ ๊ธฐ๋ฅ(๋ด๋น)
1. ์์์ ์ฅ

MarketController.java ์ผ๋ถ

```java
@PreAuthorize("hasRole('USER')")
@GetMapping("/storage") // ๋ฉ์ธํ๋ฉด -> ์ํ๋ฑ๋ก์์ ์์ฑํ๋ ๊ธ ์ด์ด์ ์์ฑํ๊ธฐ ๋ฒํผ ๋๋ ์ ๋!
public void storage(@AuthenticationPrincipal UserSecurityDto userDto, Model model) {
    // (1) ์ฌ์ฉ์ ๊ธ์์ ์์์ ์ฅ ๋ชฉ๋ก ๋ฝ๊ธฐ -> userid๋ก ์์ฑํ ๊ธ ๋ฆฌ์คํธ์(๋ด๋ฆผ์ฐจ์) -> [0]๋ฒ์งธ ๊ธ ์ ์ฅ 
    List<UsedBook> usedBookList = usedBookRepository.findByUserIdOrderByModifiedTimeDesc(userDto.getId());

    List<UsedBookPost> usedBookPost = new ArrayList<>(); // storage๊ฐ 0์ธ ๋ชฉ๋ก์ ์ ์ฅํ  ๋ฆฌ์คํธ
    for (UsedBook u : usedBookList) { // pk๋ก UsedBookPost์์ 0์ธ ๋ชฉ๋ก ์ฐพ๊ธฐ -> ๋จผ์  ๋์ค๋ ๊ฐ์ด ์ต์  ์
        UsedBookPost post = usedBookPostRepository.findByUsedBookId(u.getId());
        if (post.getStorage() == 0) {
            usedBookPost.add(post);
        }
    }

    // usedBookPost[0]๊ฐ ์ ์ผ ์ต์ ์
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

@PostMapping("/storage") // ์์์ ์ฅ ์๋ฃ ํ ๋ถ๋๋ง์ผ ๋ฉ์ธ ํ์ด์ง๋ก ์ด๋
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

2. ๋ค๋ฅธ ์ค๊ณ  ํ๋งค๊ธ ๋ธ์ถ

detail.html ์ผ๋ถ

```html
<div id="other" th:unless="${ #lists.isEmpty(otherUsedBookListFinal2) }" style="border-top: 1px solid silver;"> <!-- ์ด ์ฑ์ ๋ค๋ฅธ ์ค๊ณ  ์ํ์? -->
    <div style="font-size:18px; padding-bottom: 15px;"><span th:text="${ '<' + book.bookName + '>' }" style="font-size:21px; font-weight: bold;"></span> ๋ค๋ฅธ ์ค๊ณ  ์ํ์?</div>
    <div class="otherList align-middle" th:each="marketCreateDto : ${ otherUsedBookListFinal2 }"> <!-- ๋ค๋ฅธ ์ํ ๋์ด ๋ถ๋ถ -->
    <div style="width:200px; height: 315px;"> <!-- ์ํ๋ชฉ๋ก 1๊ฐ -->
        <a th:href="@{ /market/detail(usedBookId=${ marketCreateDto.usedBookId }) }" th:myname="${ marketCreateDto.usedBookId }" onclick="usedBookHits(this.getAttribute('myname'))">
            <img id="img" alt="" th:src="${ '/market/api/view/'+marketCreateDto.imgUsed }"  style="height:200px; width:200px; margin-bottom: 5px; object-fit: cover;">
            <div class="text-truncate" th:text="${ marketCreateDto.title }" style="font-size:17px;"></div>
            <div th:text="|${#numbers.formatInteger(marketCreateDto.price, 0, 'COMMA')}์|" style="font-weight: bold;"></div>
            <span th:text="${ marketCreateDto.level }" style="color:red; font-weight:bold;"></span>
            <small class="text-truncate" th:text="${ marketCreateDto.location }" style="display: block;"></small>
            <div class="align-middle" style="color:gray;">
                <small>
                    <i class="bi bi-heart-fill" style="font-size:10px;"></i> ๊ด์ฌ <span id="wishCount" th:text="${ marketCreateDto.wishCount }"></span> 
                    <i class="bi bi-eye" style="font-size:13px; margin-left:8px;"></i> ์กฐํ <span th:text="${ marketCreateDto.hits }"></span>
                </small>
            </div>
        </a>
    </div>
    </div>
</div>
```

MarketController.java ์ผ๋ถ

```java
// ๊ฐ์ ์ฑ ๋ค๋ฅธ ์ค๊ณ ์ํ ์์ 
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

UsedBookService.java ์ผ๋ถ

```java
// bookId๊ฐ ๋์ผํ ๋ค๋ฅธ ์ค๊ณ ์ฑ ๋ฆฌ์คํธ ๋ง๋ค๊ธฐ
public List<UsedBook> readOtherUsedBook(Integer bookId) {
    // (1) ๊ฐ์ ์ฑ์ ์ค๊ณ ํ๋งค๊ธ ๋ฆฌ์คํธ
    List<UsedBook> otherUsedBookListAll = usedBookRepository.findByBookId(bookId); 

    // (2) ์์์ ์ฅ ๊ธ ์ ์ธํ ๋ฆฌ์คํธ ์ฌ์์ฑ
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

3. ๋ธ๋ก๊ทธ ์ด๋

detail.html ์ผ๋ถ

```html
<div class="row bg-dark p-2 text-dark bg-opacity-10 rounded-4" style="height: 100px; margin: 15px 0; overflow: hidden; padding-right: none;">
    <div class="col-1" style="margin: auto;">
        <i class="bi bi-bookmarks-fill" style="font-size: 25px; margin: 30px;"></i>
    </div>
    <div class="col" style="margin: auto;"><!-- ๋ธ๋ก๊ทธ ๋ชฉ๋ก 1๊ฐ(์์) -->
        <div th:if="${ thisBookPost == null }"><!-- ํด๋น ์ฑ ๋ฆฌ๋ทฐ๊ฐ ์์์ ์ต์  ๋ฆฌ๋ทฐ ๋ณด์ฌ์ฃผ๊ธฐ -->
            <div th:if="${ latestPost == null }" style="margin: auto; font-size: 18px; font-weight: bold;"><span th:text="${ '[' + user.username + ']' }"></span>๋์ด ์์ฑํ์  ๋ฆฌ๋ทฐ๊ฐ ์์ง ์์ด์!</div>
            <div th:unless="${ latestPost == null }">
                <div style="color: gray; font-size: small;"><span th:text="${ user.username }"></span>๋์ ์ต์  ๋ฆฌ๋ทฐ โ</div>
                <a th:href="@{ /post/detail(postId=${ latestPost.postId }, bookId=${ book.bookId }, username=${ user.username }) }">
                <div class="text-truncate" style="font-weight: bold; font-size: 21px;" th:text="${ latestPost.title }"></div>
                <div style="font-size: small; color: gray;">
                    <i class="bi bi-clock"></i><span th:text="${ ' ' + #temporals.format(latestPost.modifiedTime , 'yyyy/MM/dd HH:mm') }"></span>
                </div>
                </a>
            </div>
        </div>
        <div th:unless="${ thisBookPost == null }"><!-- ํด๋น ๋ฆฌ๋ทฐ๊ฐ ์์์ ๋ณด์ฌ์ฃผ๊ธฐ -->
            <div style="color: gray; font-size: small;"><span th:text="${ user.username }"></span>๋์ <span th:text="${ '<' + book.bookName + '>' }" style="font-weight: bold;"></span> ๋ฆฌ๋ทฐ โ</div>
            <a th:href="@{ /post/detail(postId=${ thisBookPost.postId }, bookId=${ book.bookId }, username=${ user.username }) }">
            <div class="text-truncate" style="font-weight: bold; font-size: 21px;" th:text="${ thisBookPost.title }"></div>
            <div style="font-size: small; color: gray;">
                <i class="bi bi-clock"></i><span th:text="${ ' ' + #temporals.format(thisBookPost.modifiedTime , 'yyyy/MM/dd HH:mm') }"></span>
            </div>
            </a>
        </div>
    </div>
    <div class="col-4" style="margin: auto; text-align: right;">
        <a th:href="@{ /post/list(postWriter=${ user.username }) }" class="btn btn-dark" style="border: 1px solid black; padding: 16px 18px; margin:16px;"><span th:text="${ '[' + user.nickName + '] ๋์ ๋ธ๋ก๊ทธ ๊ตฌ๊ฒฝํ๊ธฐ' }"></span>
        <i class="bi bi-house-door"></i></a>
    </div>
</div>
```

MarketController.java ์ผ๋ถ

```java
// ๋ธ๋ก๊ทธ๋ก ์ฐ๊ฒฐ -> ํด๋น ์ฑ์ ๊ดํ ๋ฆฌ๋ทฐ + ์ต์  ๋ฆฌ๋ทฐ = ์ด 2๊ฐ ๋ณด์ฌ์ฃผ๊ธฐ
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

4. ๋ง์ดํ์ด์ง

MarketController.java ์ผ๋ถ

```java
@GetMapping("/mypage") // 
public void mypage(Integer userId, Model model) {
    User user = userRepository.findById(userId).get();
    List<UsedBook> usedBookAll = new ArrayList<>(); // ์ฌ์ฉ์์ ๋ฐ๋ฅธ ์ค๊ณ ์ฑ ํ๋งค ๋ฆฌ์คํธ
    List<UsedBook> usedBook = new ArrayList<>(); // usedBookAll์์ ์์์ ์ฅ ์ ์ธํ ๋ฆฌ์คํธ(๋ฉ์ธ์ ํ์๋  ๋ชฉ๋ก)
    usedBookAll = usedBookRepository.findByUserId(userId);

    for (UsedBook u : usedBookAll) {
        UsedBookPost usedBookPost = usedBookPostRepository.findByUsedBookId(u.getId());
        if (usedBookPost.getStorage() == 1) {
            usedBook.add(u);
        }
    }

    List<MarketCreateDto> list = mainList(usedBook); 
    List<UsedBookWish> wishList = usedBookWishRepository.findByUserId(userId); // ์ฌ์ฉ์๊ฐ ์ฐํ ๋ฆฌ์คํธ
    List<UsedBook> wishUsedBook = new ArrayList<>();

    for (UsedBookWish u : wishList) {
        UsedBook usedBookCHK = usedBookRepository.findById(u.getUsedBookId()).get();
        if (usedBookCHK.getId() == u.getUsedBookId()) {
            wishUsedBook.add(usedBookCHK);
        }
    }
    List<MarketCreateDto> wishListCHK = mainList(wishUsedBook); // ์ฐํ ๋ฆฌ์คํธ ์ค ํ๋ฉด์ ๋ณด์ฌ์ค ๋ชฉ๋ก

    // ๋ฆฌ๋ทฐ + ํ๋งค์ค + ํ๋งค์๋ฃ ๊ฐ์
    Integer postCount = postRepository.findByUserId(userId).size();
    Integer usedBookSoldoutCount = usedBookRepository.countUsedBookSoldoutPost(userId, "ํ๋งค์๋ฃ").size();
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

## ๊ตฌ์ฑ ํ๋ฉด
### ๋ฉ์ธ ํ์ด์ง
![main](https://user-images.githubusercontent.com/113163657/224773967-ecef716c-1824-435c-9966-b6b6db222584.JPG)

---
### ๊ฒ์ ํ์ด์ง
![search(1)](https://user-images.githubusercontent.com/113163657/224774242-64938848-2a3d-40da-8f8a-764b095cb231.JPG)

---
### ํ๋งค๊ธ ์์ธ ํ์ด์ง

---
### ๋ง์ดํ์ด์ง
![mypage(1)](https://user-images.githubusercontent.com/113163657/224774447-1e4fae3e-e4cf-4b0a-b08c-d1b7352bbdd2.JPG)
![mypage(2)](https://user-images.githubusercontent.com/113163657/224774501-9c5366c3-75c5-48af-a927-66955c51688c.JPG)
