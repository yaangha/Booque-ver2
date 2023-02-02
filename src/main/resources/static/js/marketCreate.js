/**
 * 
 */
 
 window.addEventListener('DOMContentLoaded', () => {
    
    
    const btnSearch = document.querySelector('#btnSearchM')


    btnSearch.addEventListener('click',function(){
        const keyword = document.querySelector('#keywordM').value
        create(keyword)
        
    })



    function create(keyword){
        
        axios.get('/market/search?keyword='+keyword)
        .then(response => {
            bookList(response.data)
        })
        .catch(err => {console.log(err)})
    }
    
    
    function bookList(data){
        let str='';
        str += ' <table class="w-100 table" style="text-align: center;"> ' 
            +    ' <tbody style="height: 200px;" > ' 
            
        
        for(let c of data){
            

            
        str +=    ' <tr th:each="book : ${ searchList }"> ' 
         +        ' <td class="align-middle"> ' 
         +   `<a href="/detail?id=${c.bookId}"><img src="${c.bookImage}" style="width: 80px;"/></a>  </td>` 
         +        ' </td> ' 
         +        ' <td class="align-middle" style="text-align: left;"> '  
         +                    ' <small class="d-inline-flex px-2 my-1 border rounded text-secondary"> ' 
         +                        ' <span>'+c.bookgroup+'</span><span> / </span><span>'+c.category+'</span> ' 
         +                    ' </small> ' 
         +                    ' <div class="h5">'+c.bookName+'</div> ' 
         +                    ' <div >'+c.author+'</div> ' 
         +                    ' <div >'+c.publisher+'</div> ' 
         +                    ' <div>'+c.publishedDate+'</div> ' 
         +                    ' <div>'+c.isbn+'</div> ' 
         +        ' </td> ' 


         +        ' <td class="align-middle"> ' 
                
         +        ' </td> ' 

         +        ' <td class="align-middle"> ' 
         +            ' <form id="formMarket" action="/market/create" > ' 
         +                ' <input type="hidden"  name="bookId" value="'+c.bookId+'"/> ' 
         +      '<button class="btn btn-dark btn-sm my-2" style="width: 200px;" type="button" id="btnMarket" value="'+c.bookId+'">부끄마켓 판매하기</button>' 
         +            ' </form> ' 
         +        ' </td> ' 
         +    ' </tr> ' 
        } 
         
      str +=   ' </tbody> ' 
          +   ' </table> ' ;  
        
        
        const bookList = document.querySelector('#bookList')
        
        bookList.innerHTML=str;   
        
        
    const btnMarket = document.querySelectorAll('#btnMarket') 
    btnMarket.forEach(btn =>{
        
        btn.addEventListener('click', function(){
            
            axios.get('/market/createUsed?bookId='+btn.value)
                .then(response =>{
                    alert('저장이 되었습니다.')
                    // bookList는 지우고, 선택 된 책만 보여줌.
                    bookList.innerHTML='';
                    
                    // Map을 통해서 리턴하면 여러개의 타입을 받을 수 있음. key 값만 주면 됨.
                    console.log(response.data.book)
                    console.log(response.data.usedBookId)
                    bookSelect(response.data.book, response.data.usedBookId)
                        
                })
            
        })
    })
    

        
        
    }
    
    /** 판매할 책 선택 후 html에서 보여줄 선택된 책의 정보!
        c는 book정보, a는 UsedBook의 PK
     */
    function bookSelect(book,usedBookId){
        // str로 만들기 
        const bookSelectDiv = document.querySelector('#bookSelect')
        let str='';
        
        str += ' <table class="w-100 table" style="text-align: center;"> ' 
            +    ' <tbody style="height: 200px;" > ' 

         +  ' <tr > ' 
         +        ' <td class="align-middle"> ' 
         +   `<a href="/detail?id=${book.bookId}" ><img src="${book.bookImage}" style="width: 80px;"/></a>  </td>` 
         +        ' </td> ' 
         +        ' <td class="align-middle" style="text-align: left;"> '  
         +                    ' <small class="d-inline-flex px-2 my-1 border rounded text-secondary"> ' 
         +                        ' <span>'+book.bookgroup+'</span><span> / </span><span>'+book.category+'</span> ' 
         +                    ' </small> ' 
         +                    ' <div class="h5"><input readyonly style="border:none; " class="w-100" form="formCreate" name="bookTitle"  value="'+book.bookName+'"/></div> ' 
         +                    ' <div ><span> 작가 : </span>'+book.author+'</div> ' 
         +        ' <td class="align-middle" style="text-align: left;"> '  
         +          '<small>출판사</small>'
         +                    ' <div > '+book.publisher+'</div> ' 
         +                    ' <div>'+book.publishedDate+'</div> ' 
         +        ' </td> ' 
         +        ' <td class="align-middle" style="text-align: left;"> '  
         +                    ' <div> isbn : '+book.isbn+'</div> ' 
         +        ' </td> ' 


         +        ' <td class="align-middle"> ' 
                
         +        ' </td> ' 

//         +        ' <td class="align-middle"> ' 
//         +                ' <input class="btn btn-dark btn-sm my-2"  style="width: 100px;"   value="뭐 넣지"> '
//         +        ' </td> ' 
         +    ' </tr> ' 
         +   ' </tbody> ' 
          +   ' </table> ' ;  
        
        bookSelectDiv.innerHTML = str;
        document.getElementById('marketCreate').style.display='none';
        
        const idDiv = document.querySelector('#usedId')
        let used = '<input type="text" name=usedBookId value="'+usedBookId+'">';
        idDiv.innerHTML = used
        
    }
    
    
    
})