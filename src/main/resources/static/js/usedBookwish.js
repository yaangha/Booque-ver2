/**
 *
 */
window.addEventListener('DOMContentLoaded', () => {
    
    // 수정 필요!! 지금 찜하기 버튼 안되는 상태!! 
    
    // 찜하기 버튼 클릭시 DB 업데이트 -> 페이지 찜하기 정보 업데이트
    const btnUsedBookWish = document.querySelector('#btnUsedBookWish');
    btnUsedBookWish.addEventListener('click', saveUsedBookWish);
    
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