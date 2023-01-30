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
        
        axios.get('/market/searchT?keyword='+keyword)
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
         +   `<a href="/detail?id=${c.bookId}" onclick="viewHitUp(${c.bookId}, ${username});"><img src="${c.bookImage}" style="width: 80px;"/></a>  </td>` 
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
         +                ' <input class="btn btn-dark btn-sm my-2" id="btnMarke" style="width: 100px;"  type="submit" value="부끄마켓"> '
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
            console.log('클릭')
            console.log(btn.value)
            axios.get('/market/createM?bookId='+btn.value)
                .then(response =>{
                    alert('저장이 되었습니다.')
                    // 메서드 만들고 디비 저장 하나만 보이게
                    // div 만들기
                    bookList.innerHTML='';
                    console.log(response.data.book)
                    console.log(response.data.usedBookId)
                    
                    bookSelect(response.data.book, response.data.usedBookId)
                        
                })
            
        })
    })
    

        
        
    }
    
    
    function bookSelect(c,a){
        // str로 만들기 
        const bookSelectDiv = document.querySelector('#bookSelect')
        let str='';
        
        str += ' <table class="w-100 table" style="text-align: center;"> ' 
            +    ' <tbody style="height: 200px;" > ' 

         +  ' <tr > ' 
         +        ' <td class="align-middle"> ' 
         +   `<a href="/detail?id=${c.bookId}" onclick="viewHitUp(${c.bookId}, ${username});"><img src="${c.bookImage}" style="width: 150px;"/></a>  </td>` 
         +        ' </td> ' 
         +        ' <td class="align-middle" style="text-align: left;"> '  
         +                    ' <small class="d-inline-flex px-2 my-1 border rounded text-secondary"> ' 
         +                        ' <span>'+c.bookgroup+'</span><span> / </span><span>'+c.category+'</span> ' 
         +                    ' </small> ' 
         +                    ' <div class="h5">'+c.bookName+'</div> ' 
         +        ' </td> ' 
         +        ' <td class="align-middle" style="text-align: left;"> '  
         +                    ' <div ><span> 작가 : </span>'+c.author+'</div> ' 
         +        ' </td> ' 
         +        ' <td class="align-middle" style="text-align: left;"> '  
         +          '<small>출판사</small>'
         +                    ' <div > '+c.publisher+'</div> ' 
         +                    ' <div>'+c.publishedDate+'</div> ' 
         +        ' </td> ' 
         +        ' <td class="align-middle" style="text-align: left;"> '  
         +                    ' <div> isbn : '+c.isbn+'</div> ' 
         +        ' </td> ' 


         +        ' <td class="align-middle"> ' 
                
         +        ' </td> ' 

         +        ' <td class="align-middle"> ' 
         +                ' <input class="btn btn-dark btn-sm my-2"  style="width: 100px;"   value="뭐 넣지"> '
         +        ' </td> ' 
         +    ' </tr> ' 
         +   ' </tbody> ' 
          +   ' </table> ' ;  
        
        bookSelectDiv.innerHTML = str;
        document.getElementById('marketCreate').style.display='none';
        
        const idDiv = document.querySelector('#usedId')
        let used = '<input type="text" name=usedBookId value="'+a+'">';
        idDiv.innerHTML = used
        
    }
    
    
    
})