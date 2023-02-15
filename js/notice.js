/**
 * 
 */
window.addEventListener('DOMContentLoaded', () => {
    
    if(userId){
        
         showNotice();      // 로그인 한 유저의 알림 리스트
    }
  
    
    function showNotice(){
        const userId = document.querySelector('#userId').value;
       
        axios
        .get('/showNotice/' + userId)  
        .then(response => { updateNoticeList(response.data) } )
        .catch(err => { console.log(err) });
        
    }    
    
     
    
    function updateNoticeList(data){
        
         const noticeCount = document.querySelector('#noticeCount');
         let count = '';
         count += '<span style="color: white;">'+ data.length +'</span>';
         noticeCount.innerHTML = count;
        
        const divNotices = document.querySelector('#divNotices');
        let str ='';
        
         for (let x of data){ 
             
            if(x.replyId) {
              str +=`<div><a style="font-size: 17px; text-align:left; padding-top:15px; color:#708090;" class="w3-bar-item w3-button"`
                  + `onclick="deleteNotice();" a href="/post/detail?postId=${ x.postId }&bookId=${ x.bookId }&replyId=${ x.replyId }">`
                  + '<input type="hidden" id="noticeId"  value="'+ x.noticeId +'" />'
                  + '내블로그) <img class="rounded-circle m-1" width="30" height="30" src="' + x.userImage + '" />'
                  + `<span class="under-line"><span class="fw-bold">${x.nickName}</span>님의 새 댓글!</span>`
                  + '</a></div>';
            }
               
            if(x.usedBookId){
             str +=`<div><a style="font-size: 17px; text-align:left; padding-top:15px; color:#708090;" class="w3-bar-item w3-button"`
                 + `onclick="deleteNotice();" a href=" /market/detail?usedBookId=${ x.usedBookId }">`
                 + '<input type="hidden" id="noticeId"  value="'+ x.noticeId +'" />'
                 + '부끄장터) <img class="rounded-circle m-1" width="30" height="30" src="' + x.bookImage + '" />'
                 + `<span class="fw-bold">${x.bookName}</span> 새 판매글!`
                 + '</a></div>';
          
            }  
        }
        
        divNotices.innerHTML = str;
    
    
    }
  

});