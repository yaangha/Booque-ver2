/**
 * 
 */
 
 window.addEventListener('DOMContentLoaded', () => {
const btnSubmit = document.querySelector('#btnSubmit');

btnSubmit.addEventListener('click', function () {

    const sample3_address = document.querySelector('#sample3_address').value;
    const title = document.querySelector('#title').value;
    const price = document.querySelector('#price').value;
    const contents = document.querySelector('#contents').value;
    
    
    const uploads = document.querySelector('#uploads');
    const files = uploadResults.querySelectorAll('img');
    
    let htmlStr = '';
    files.forEach(x => {
        const imgLink = x.getAttribute('data-src');
        //console.log(imgLink);
        htmlStr += `<input type="hidden" name="fileNames" value="${imgLink}" />`;
    });
    uploads.innerHTML = htmlStr;
    
    
    
    if (sample3_address == '' || title == '' || price == '' || contents == '') {
        alert('필수항목을 모두 채워주세요!');
        return;
    }
    
    
    
    
    const result = confirm('등록하시겠습니까?');
    if (result) {
     document.querySelector('#formCreate').submit();
        formCreate.action = '/market/create';
        formCreate.method = 'post';
        formCreate.submit();
        
        checkContainingKeyword();
    }
    
    
});
    
   // function checkKeyword(){
   //    const contents = document.querySelector('#contents').value;
   //     const usedBookId = document.querySelector('#usedBookId').value;
   //     
   //     const data ={
   //          contents : contents,
   //          usedBookId : usedBookId
   //     }
   //      
   //     console.log(체크데이터);
   //    console.log(data);
   //     axios
   //      .get('/notice/check', data)
   //      .then(response => { newKeywordNotice(response.data) } )
   //       .catch(err => { console.log(err); });
   // };
    
    
    
    
    

      const uploadResults = document.querySelector('#uploadResults');
    
    document.querySelector('#btnModalUpload').addEventListener('click', e =>{
        console.log('btnModal 누름')
        
        const formData = new FormData();  
        const fileInput =  document.querySelector('input[name="files"]');
        
        Array.from(fileInput.files).forEach(f => {
            formData.append('files', f);
            
        })
        
        

        uploadFiles(formData);
        
        
    })
    

    function uploadFiles(formData){
        
        axios.post('/market/api/upload', formData)
            .then(getUploaded)
            .catch(err => { console.log(err)})
        
        const divEx = document.querySelector('#explain')
        divEx.innerHTML ='';
        
        
    }
    
    function getUploaded(response) {
        if (response.status == 200) {
            
        const imgNum = document.querySelector('#imgNum');
        imgNum.innerHTML = response.data.length
        
            response.data.forEach(x => {
                //console.log(data);
                // 이미지 파일이 아닌 경우, 디폴트 썸네일 이미지를 사용하도록.
                let img = '';
                    img = `<img src="/market/api/view/${x.link}" data-src="${x.uuid + '_' + x.fileName}" class="card-img-bottom"/>`;
                const htmlStr = `
                                <div class="card" style="width:300px; margin: 5px;">
                                    <div class="card-header d-flex justify-content-center">
                                        ${x.fileName}
                                        <button class="btnDelet btn-close" aria-label="Close"
                                            data-uuid="${x.uuid}" data-fname="${x.fileName}"></button>
                                    </div>
                                    <div class="card-body">
                                        ${img}
                                    </div>
                                </div>`;
                
                uploadResults.innerHTML += htmlStr;
                
            });
            
            
     
            
            document.querySelectorAll('.btnDelet').forEach(btn => {
                btn.addEventListener('click', removeFileFromServer);
            });
            
        }
    }
    
    function removeFileFromServer(event){
        event.preventDefault();
        
        const btn = event.target;
        const uuid = btn.getAttribute('data-uuid');
        const fname = btn.getAttribute('data-fname');      
        const fileName = uuid+'_'+fname;  
        console.log(uuid);
        console.log(fname);
        
        axios.delete('/market/api/view/'+fileName)
            .then(btn.closest('.card').remove())
            
        
        
    }
    
    
    
    
    
    
    
    
    
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
         +   `<a href="/detail?id=${c.bookId}"><img src="${c.bookImage}" style="width: 150px;"/></a>  </td>` 
         +        ' </td> ' 
         +        ' <td class="w-100 align-middle" style="text-align: left;"> '  
         +                    ' <small class="d-inline-flex px-2 my-1 border rounded text-secondary" style="font-size: 12px;"> ' 
         +                        ' <span>'+c.bookgroup+'</span><span> / </span><span>'+c.category+'</span> ' 
         +                    ' </small><br> ' 
         +                    ' <div class="h5" style="font-weight: bold;">'+c.bookName+'</div><br> ' 
         +                    ' <div >'+c.author+'</div><br> ' 
         +                    ' <div >'+c.publisher+'</div><br> ' 
         +                    ' <div>'+c.publishedDate+'</div><br> '   
         +                    ' <div>ISBN: '+c.isbn+'</div> ' 
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
                .catch(err => {
                    console.log(err)
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
        
        str += ' <table class="w-100" style="text-align: center;"> ' 
            +    ' <tbody style="height: 200px;" > ' 

         +  ' <tr > ' 
         +        ' <td class="align-middle"> ' 
         +   `<a href="/detail?id=${book.bookId}" ><img src="${book.bookImage}" style="width: 200px;"/></a>  </td>` 
         +        ' </td> ' 
         +        ' <td class="align-middle" style="text-align: left; padding-left: 25px;"> '  
         +                    ' <small class="d-inline-flex px-2 my-1 border rounded text-secondary" style="font-size: 12px;"> ' 
         +                        ' <span>'+book.bookgroup+'</span><span> / </span><span>'+book.category+'</span> ' 
         +                    ' </small><br> ' 
         +                    ' <div class="h5"><input readyonly style="border:none; width:800px; font-weight: bold;" form="formCreate" name="bookTitle"  value="'+book.bookName+'"/></div><br> ' 
         +                    ' <div ><span> 작가 : </span>'+book.author+'</div><br> ' 
         +                    '<small>출판사</small>'
         +                    ' <div > '+book.publisher+'</div><br> ' 
         +                    ' <div>'+book.publishedDate+'</div><br> '
         +                    ' <div>ISBN : '+book.isbn+'</div> ' 
         +    ' </tr> ' 
         +   ' </tbody> ' 
          +   ' </table> ' ;  
        
        bookSelectDiv.innerHTML = str;
        document.getElementById('marketCreate').style.display='none';
        
        const idDiv = document.querySelector('#usedId')
        let used = '<input type="text" id="usedBookId" name=usedBookId value="'+usedBookId+'">';
        idDiv.innerHTML = used
        
    }
    
    
    
})