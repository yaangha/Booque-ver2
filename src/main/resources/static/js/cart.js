/**
 * 
 */
 window.addEventListener('DOMContentLoaded', () => {
    
    // TODO 필요 페이지에 장바구니 버튼 id=btnCart 통일하여 추가
    // TODO count hidden으로 추가하기(detail 페이지 제외)
    
    // 장바구니 버튼 누르면 DB 저장 -> 모달창에선 장바구니 이동여부만 체크
    const btnCart = document.querySelector('#btnCart');
    const btnGoCart = document.querySelector('#btnGoCart');
    
    btnCart.addEventListener('click', saveCart);
    btnGoCart.addEventListener('click', saveCart);
    
    function saveCart() {
        const bookId = document.querySelector('#id').value;
        const count = document.querySelector('#count').value;
        
        const data = {
            bookId: bookId,
            count: count
        }
        
        axios.post('/api/cartAdd', data)
            .then(response => {
                console.log(response);
            })
            .catch(error => {
                console.log(error)
            })
    } // saveCart end
    
}) // js end