
 // (지혜) 사이드바 열고 닫기
function w3_open() {
  document.getElementById("sideBar").style.display = "block";
  document.getElementById("overlay").style.display = "block";
}
 
function w3_close() {
  document.getElementById("sideBar").style.display = "none";
  document.getElementById("overlay").style.display = "none";
}

 
 
 // (지혜) 사이드바 Accordion 메뉴
 
function open_group0() {
    let x = document.querySelector('#group0');
    let y = document.querySelector('#group1');
    let z = document.querySelector('#group2');
    
    if (x.className.indexOf("w3-show") == -1) {   // 해당 메뉴가 닫혀 있으면(class에 w3-show 속성이 없으면?)
        x.className += " w3-show";   // w3-show 속성을 추가해 열어 주기
        y.className = y.className.replace(" w3-show", "");   // 다른 메뉴는 닫기
        z.className = z.className.replace(" w3-show", "");   // 다른 메뉴는 닫기
    } else {
        x.className = x.className.replace(" w3-show", "");   // 열려 있다면 닫아 주기(w3-show 속성 지우기)
    }
}
 
function open_group1() {
  let x = document.querySelector('#group1');
  let y = document.querySelector('#group0');
  let z = document.querySelector('#group2');
    
  if (x.className.indexOf("w3-show") == -1) {
    x.className += " w3-show";
    y.className = y.className.replace(" w3-show", "");
    z.className = z.className.replace(" w3-show", "");
  } else {
    x.className = x.className.replace(" w3-show", "");
  }
}

function open_group2() {
  let x = document.querySelector('#group2');
  let y = document.querySelector('#group0');
  let z = document.querySelector('#group1');
  
  if (x.className.indexOf("w3-show") == -1) {
    x.className += " w3-show";
    y.className = y.className.replace(" w3-show", "");
    z.className = z.className.replace(" w3-show", "");
  } else {
    x.className = x.className.replace(" w3-show", "");
  }
}


// (지혜) 사이드바 Accordian 하위 메뉴 선택시 효과

window.addEventListener('DOMContentLoaded', function () {
    const buttons = document.querySelectorAll('.btnCategory');   // 모든 하위카테 버튼 찾기
    const cateNows = document.querySelectorAll('.cateNow');   // 모든 하위카테 앞의 ▶ 표시 찾기
    
    buttons.forEach(btn => {
        btn.addEventListener('click', e => {
            
            buttons.forEach(others => {
                others.className = others.className.replace(" w3-light-grey", "");  // 선택한 하위카테 외의 다른 카테는 모두 회색배경 지우기
            });
            
            cateNows.forEach(others2 => {
               others2.style.display = "none";  // 선택한 하위카테 외의 다른 카테는 모두 ▶ 표시 지우기 
            });
            
            const div = btn.closest('div');
            const a = div.querySelector('a');
            const i = div.querySelector('i');
            a.className += " w3-light-grey";    // 선택한 하위카테에만 회색배경 넣기
            i.style.display = "inline";  // 선택한 하위카테 앞에만 ▶ 표시 붙이기
        });
    });
});



// (지혜) 책 카테고리별 페이지 이동시 사이드바에 표시 효과(현재 보고 있는 카테고리 강조)


window.addEventListener('DOMContentLoaded', function () {
    
    const pathname = window.location.pathname;   // 주소창 '?' 앞의 pathname 찾기 (/category)
    const url = window.location.search;  // 주소창 '/category' 뒤에 오는 텍스트 찾기(예: ?group=외국도서&category=경제)
    const buttons = document.querySelectorAll('.btnCategory');   // 모든 하위카테 버튼 찾기
    const cateNows = document.querySelectorAll('.cateNow');   // 모든 하위카테 앞의 ▶ 표시 찾기
    const btnGroup = document.querySelectorAll('.btnGroup');  // 모든 아코디언메뉴 조상 찾기(분야별도서/국내도서/외국도서)
    
    buttons.forEach(others => {
        others.className = others.className.replace(" w3-light-grey", "");   // 다른 버튼들 배경색 제거
    });
   
    cateNows.forEach(others2 => {
        others2.style.display = "none";  // 다른 버튼들 앞의 ▶ 표시 제거
    });
    
    buttons.forEach(btn => {
       const div = btn.closest('div');
       const groupdiv = div.closest('.btnGroup');
       const a = div.querySelector('a');
       const i = div.querySelector('i');
       const href = a.getAttribute('href');  // 선택된 버튼의 href값 찾기
       
       if ((pathname+url) == href) {      // 주소창의 주소와 동일한 href 경로명을 가진 버튼 찾아서(=현재 선택된 버튼 찾아서)
       
       btnGroup.forEach(others3 => {
       others3.className = others3.className.replace(" w3-show", "");   // 다른 열려 있는 아코디언메뉴들 접기
        });    // 여기에 넣은 이유는, 카테가 선택되지 않는 이상 '분야별도서' 아코디언메뉴는 늘 열어놓을 것이기 때문
       
         a.className += " w3-light-grey";  // 배경색 강조
         i.style.display = "inline";  // 버튼 앞에 ▶ 붙이기
         groupdiv.className += " w3-show";  // 현재 선택된 하위카테의 아코디언 조상(분야별도서/국내도서/외국도서) 펼치기
         document.getElementById("sideBar").style.display = "block";   // 사이드바 열어 두기(연속적으로 다른 카테 들어가기 편하도록)
         document.getElementById("overlay").style.display = "block";
        };
    });
    
    
    /*
    $('div').find('.btnCategory').each(function() {
        const this = document.querySelector($(this)).getAttribute('href');
        alert();
        
        buttons.forEach(others => {
           others.className = others.className.replace(" w3-light-grey", ""); 
           
           
        });
        
        $(this).className += " w3-light-grey";
        console.log(this.className);
    });*/
    
});



// (지혜) 검색창 내려오기/닫기
function search_open() {
  document.getElementById("searchBar").style.display = "block";
  document.getElementById("overlay2").style.display = "block";
}
 
function search_close() {
  document.getElementById("searchBar").style.display = "none";
  document.getElementById("overlay2").style.display = "none";
}



// (지혜) 새 창으로 띄우기
// 채팅 기능에 사용 중 - 상단바 채팅버튼 & 부끄마켓 상세페이지창 채팅하기버튼
    function openWindow(url) {
        ChatWindow = window.open("about:blank", "Chat", 
            "width=1200,height=700,left=350,top=150,dependent=yes,location=no, menubar=no,status=no,resizable=no,toolbar=no,scrollbars=yes");
        ChatWindow.location.href = url;
        ChatWindow.focus();
        return false;
    }



