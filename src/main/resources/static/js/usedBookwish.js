/**
 *
 */
window.addEventListener('DOMContentLoaded', () => {
    
    // 수정 필요!! 지금 찜하기 버튼 안되는 상태!! 
    
    // 찜하기 버튼 클릭시 DB 업데이트 -> 페이지 찜하기 정보 업데이트
    const btnUsedBookWish = document.querySelector('#btnUsedBookWish');
    btnUsedBookWish.addEventListener('click', function(){
        const usedBookId = document.querySelector('#usedBookId').value;
        console.log(usedBookId);
        console.log('버튼 클릭');
        
        axios.get('/market/api/usedBookWish?usedBookId='+usedBookId)
            .then(response =>{
                console.log('컨트롤러로 넘어감')
                console.log(response.data)
                chaingHeart(response.data)
            })
        
        
        
    });
    
    
    
    function chaingHeart(h){
        const noWish = document.querySelector('#noWish')
        const yesWish = document.querySelector('#yesWish')
        
        if(h){
            yesWish.style.display = "block";
            noWish.style.display = "none";
        }else{
            yesWish.style.display = "none";
            noWish.style.display = "block";
            
        }
        
    }
    
    
    function saveUsedBookWish() {
        const usedBookId = document.querySelector('#usedBookId').value * 1;
        
        console.log(usedBookId)

        axios.post('/api/usedBookWish/', usedBookId)
            .then(response => {
                console.log(response);
                alert('성공!!');
            })
            .catch(error => {
                console.log(error)
                alert('실패!!');
            })
    }
});