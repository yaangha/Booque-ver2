/**
 * 
 */
window.addEventListener('DOMContentLoaded', () => {
    
    if(userId){
    showNotice();
}
  
    function showNotice(){
        const userId = document.querySelector('#userId').value;
        console.log('쇼노티스유저아이디');
        console.log(userId);
       
        axios
        .get('/showNotice/' + userId)  
        .then(response => { updateNoticeList(response.data) } )
        .catch(err => { console.log(err) });
        
    }    
    
    function updateNoticeList(data){
        console.log('리스트?');
        console.log(data.length);
        
         const noticeCount = document.querySelector('#noticeCount');
         let count = '';
         count += '<span style="color: white;">'+ data.length +'</span>';
         noticeCount.innerHTML = count;
        
        const divNotices = document.querySelector('#divNotices');
        let str ='';
        
         for (let x of data){ 
             
              str +=`<div><a style="font-size: 17px; text-align:left; padding-top:15px; color:#708090;" class="w3-bar-item w3-button"`
                  + `onclick="deleteNotice();" a href="/post/detail?postId=${ x.postId }&bookId=${ x.bookId }&replyId=${ x.replyId }">`
                  + '<input type="hidden" id="noticeId"  value="'+ x.noticeId +'" />'
                  + '<img class="rounded-circle m-1" width="30" height="30" src="' + x.userImage + '" />'
                  + `<span class="under-line"><span class="fw-bold">${x.nickName}</span>님의 새 댓글이 있어요!</span>`
                  + '</a></div>';
             
        }
        
        divNotices.innerHTML = str;
    
    
    }
  
    


});