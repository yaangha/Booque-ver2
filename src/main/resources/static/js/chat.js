/**
 * 
 */
 
window.addEventListener('DOMContentLoaded', () => {
    
        var stompClient = null;
        var sender = $('#loginUser').val();
        var chatRoomId = $('#chatRoomId').val();
        var seller  = $('#seller').text();
        var chatWithImage = $('#chatWithImage').val();
        // invoke when DOM(Documents Object Model; HTML(<head>, <body>...etc) is ready
        $(document).ready(connect());
        
        function connect() {
            // map URL using SockJS 
            var socket = new SockJS('/chat');
            var url = '/user/' + chatRoomId + '/queue/messages';
            var url2 = '/user/' + chatRoomId + '/queue/notification/'+ seller;
            // webSocket 대신 SockJS을 사용하므로 Stomp.client()가 아닌 Stomp.over()를 사용함
            stompClient = Stomp.over(socket);
            // connect(header, connectCallback(==연결에 성공하면 실행되는 메서드))
            stompClient.connect({}, function() { 
                registration();
                autoScroll();
                // url: 채팅방 참여자들에게 공유되는 경로
                // callback(function()): 클라이언트가 서버(Controller broker)로부터 메시지를 수신했을 때(== STOMP send()가 실행되었을 때) 실행
                stompClient.subscribe(url, function(output) { // 메세지에 관한 구독
                    isOnline();
                    // JSP <body>에 append할 메시지 contents
                    showBroadcastMessage(createTextNode(JSON.parse(output.body)));
                    autoScroll();
                });
                stompClient.subscribe(url2, function(output){ // 메세지 읽음 알림에 관한 구독
                    read(); 
                });
                }, 
                    // connect() 에러 발생 시 실행
                        function (err) {
                            alert('error' + err);
            });
 
        };
        
        // WebSocket broker 경로로 JSON 타입 메시지데이터를 전송함
        function sendChat(json) {
            stompClient.send("/app/chat/"+ chatRoomId, {}, JSON.stringify(json));
        }
        
        
        const btnSend = document.querySelector('#btnSend');
        const messageInput = document.querySelector('#message');
        
        // (지혜) 메시지 입력해야 보내기버튼 활성화되기
        messageInput.addEventListener('keyup', activateBtnSend);
        
        btnSend.disabled = true;
        
        function activateBtnSend() {
            
            const messageValue = document.querySelector('#message').value;
            
            console.log('activateBtnSend 함수 - value: '+messageValue);
            
            if ($.trim(messageValue) == '') {
                // if (messageValue.replace(/\s|　/gi, "").length == 0) {
                btnSend.disabled = true;
                btnSend.style.color = "silver";
            } else {
                btnSend.style.color = "dodgerblue";
                btnSend.disabled = false;
            }
            
        };
        
        
        // 보내기 버튼 클릭시 실행되는 메서드
        btnSend.addEventListener('click', send);
        
        const date = new Date();
            
        function send() {
            var message = $('#message').val();
            autoScroll();
            sendChat({
                // 'chatRoomId': chatRoomId,
                // 'message': message,
                // 'buyerId': buyerId, 
                // 'sellerId': sellerId,
                // 'usedBookId': usedBookId,
                'sender': sender,
                'message': message,
                'sendTime': getCurrentTime()
                });   
            $('#message').val("");
            // 보낸 후 보내기버튼 비활성화
            btnSend.disabled = true;
            btnSend.style.color = "silver";
            
            const userId = document.querySelector('#userId').value;
            sendChatList(userId)
            
        }
        
        function sendChatList(userId){
            axios.get('/chat/api/list?userId='+ userId)
                .then(response => {
                    
                        sendBychatList(response)
                    })
                .catch(err => {
                    console.log(err)
                })
            
        }
        
        function sendBychatList(response){
            const chatListTbody = document.querySelector('#chatListTbody')
            chatListTbody.innerHTML = '';
 
            
            response.data.forEach(x => {
             //   x[0].style.backgroundColor = "seashell";
                console.log(x)
                
               const htmlStr = `
                            <tr  class="btnChatRoom" 
                            onclick="location.href='/chat?chatRoomId=${ x.chatRoomId}'"
                            style="cursor:pointer; background-color:none; width: 270px;">
                                <td style="padding-left:10px; width:20%;">
                                    <img class="rounded-circle" width="40" height="40" src = "${ x.chatWithImage }" />
                                </td>
                                <td style="padding-left: 10px; padding-right:10px; width:80%;">
                                    <div >${ x.chatWithName }</div>
                                    <div style="font-size:9px;" >${ x.modifiedTime }</div>
                                    <span class="lastMessage" >${ x.lastMessage }</span>
                                </td>
                                <td style="padding-right:10px; width:20%;">
                                     <img width="auto" height="50" src = "${ '/market/api/view/'+ x.usedBookImage }" />
                                </td>
                                <td>
                                    <input type="hidden" class="usedBookTitle" value="${x.usedBookTitle}"/>
                                </td>
                            </tr>
                `
                
            chatListTbody.innerHTML += htmlStr
                
            })
            
        const chatTr = document.querySelectorAll('.btnChatRoom')
        chatTr[0].style.backgroundColor = "seashell";
        }
        
        
        
        
        
        
        
        // 메시지 입력 창에서 Enter키가 보내기와 연동되도록 설정
        var inputMessage = document.getElementById('message'); 
        inputMessage.addEventListener('keyup', function enterSend(event) {
            
            let inputMessageValue = inputMessage.value;
            
            if (event.keyCode === null) {
                event.preventDefault();
            }
            
            if ((event.keyCode === 13) && ($.trim(inputMessageValue) != '')) {
                // if ((event.keyCode === 13) && (input.replace(/\s|　/gi, "").length != 0)) {
                send();
            }
        });
        
        
        // (홍찬) 현재 시간 출력(채팅 주고 받을 때 보낸 시각, 받은 시각 출력)
        function getCurrentTime() {
        return new Date().toLocaleString();
        }
        
        // 입력한 메시지를 HTML 형태로 가공
//        function createTextNode(messageObj) {
//            if(messageObj.sender == sender){
//                return '<p><div align="right" id="newHistory"><div style="margin-left: 60%;">' +
//            messageObj.message+
//            '</div><div style="font-size:13px; color:grey; margin-left: 60%;">' +
//            messageObj.sendTime +
//            '</div><span id="reads" style="color:dodgerblue;">1</span></div></p>';
//            } else {
//            return '<div id="newResponseHistory" class="alert alert-info"><div style="float:left;">' +
//            '<img class="rounded-circle" width="40" height="40" src="' +
//           chatWithImage + 
//            '" style="margin-right:10px;"></div><div style="margin-right: 60%;"><span>' +
//            messageObj.message+
//            '</span><br/></div><div style="font-size:13px; color:grey;  margin-right: 60%;"><span>' +
//            messageObj.sendTime +
//            '<span><br/><br/></div></div>';
//            }
//        }
        
        function createTextNode(messageObj) {
            if(messageObj.sender == sender){ // 채팅을 보내는 사람
                return '<div style="width: 500px;"><div style="text-align: right; margin-left: 480px; width:270px;" id="newHistory"><div style="text-align: right; width:270px;">' +
            messageObj.message+
            '</div><div style="width: 270px; text-align: right: font-size:13px; color:grey;">' +
            messageObj.sendTime +
            '</div><div id="reads" style="color:dodgerblue;">1</div></div></div>';
            } else { // 채팅을 받는 사람
            return '<div id="newResponseHistory" class="alert alert-info"><div style="width: 40px; margin-right: 15px; display: inline-block; float: left;">' +
            '<img class="rounded-circle" width="40" height="40" src="' +
            chatWithImage + 
            '" style="margin-right:10px;"></div><div style="width: 270px; text-align: left; display: inline-block;"><div>' +
            messageObj.message+
            '</div><div style="font-size:13px; color:grey;">' +
            messageObj.sendTime +
            '</div><br/><br/></div></div>';
            }
        }
        
        // 채팅 입력창에 포커싱 맞췄을 때 발동
        $('#message').focus(function(){
            const nm = document.querySelectorAll('#newResponseHistory');
            for (var i = 0; i < nm.length; ++i) {
            nm[i].className = "";
            nm[i].removeAttribute('id');
            }
            stompClient.send("/app/chat/read/"+ chatRoomId, {}, JSON.stringify(
            {sender:sender}
        ))
        });
        
        // (홍찬) 채팅칠 때 혹은 채팅받을 때 자동으로 스크롤 조절 해줌.
        function autoScroll() {
        $chatHistory = $('#content');
        $chatHistory.scrollTop($chatHistory[0].scrollHeight);
        }
        
        // HTML 형태의 메시지를 화면에 출력해줌
        // 해당되는 id 태그의 모든 하위 내용들을 message가 추가된 내용으로 갱신해줌
        function showBroadcastMessage(message) {
            $("#content").html($("#content").html() + message);
        }
        
        // 대화를 시작하는 대상 등록
        function registration(){
        $.get("/registration/" + sender)
        .fail(err =>(console.log(err)))
        };
        
        // 창 닫기 버튼 이벤트(로그아웃하는 시점)
        window.addEventListener('unload',function(){
            $.get("/unregistration/" + sender)
            .fail(err => console.log("창닫기 오류"))
        });
        
        
        // 상대방이 온라인/오프라인 확인
        function isOnline(){
            const data = {
            nickName: seller,
            chatRoomId: chatRoomId
        }
            $.post("/onlineChk", data)
            .done(response => { 
                if(response == 1){ // true: 상대가 online
                     
                }
            })
            .fail(err => console.log("온라인/오프라인 오류"))
        }
        
        //read();
        // 읽음 = 0 안읽음 = 1 체크
        function read(){
            const read = document.querySelectorAll('#reads');
            for (var i = 0; i < read.length; ++i) {
            read[i].style.visibility = 'hidden';
            read[i].removeAttribute('id');
            }
            
        }
        
        
    // (지혜) 선택된 채팅창 배경색 바꾸기    
    const pathname = window.location.pathname;   // 주소창 '?' 앞의 pathname 찾기 (/chat)
    const url = window.location.search;  // 주소창 '/chat' 뒤에 오는 텍스트 찾기(예: ?chatRoomId=35)
    const btnChatRoom = document.querySelectorAll('.btnChatRoom');   // 좌측 채팅방 리스트에서 각각의 채팅방 블럭 찾기
    
    btnChatRoom.forEach(others => {
        others.style.backgroundColor = "";  // 다른 채팅방 블럭들의 배경색 제거
    
    btnChatRoom.forEach(btn => {
       const href = btn.getAttribute('onclick');  // 선택된 채팅방 블럭의 href값 찾기
       if (("location.href='" + pathname + url + "'") == href) {      // 주소창의 주소와 동일한 href 경로명을 가진 방 찾아서(=현재 선택된 방 찾아서)
         btn.style.backgroundColor = "seashell";   // 배경색 바꿔 주기
        };
    });
    });
    
    
    
    // (지혜) 안읽은 채팅방 필터링
    const btnShowAllChats = document.querySelector('#showAllChats');
    const btnShowUnreadChats = document.querySelector('#showUnreadChats');

    btnShowAllChats.addEventListener('click', showAll);
    btnShowUnreadChats.addEventListener('click', showUnread);

    function showAll() {    // 전체 채팅방 보기
        let chatRoom = document.querySelectorAll('.btnChatRoom');
        for (i = 0; i <= chatRoom.length; i++) {
            chatRoom[i].style.display = "block";
            }
    }

    function showUnread() {
        let chatRoom = document.querySelectorAll('.btnChatRoom');
        let unread = document.querySelectorAll('.unread');
        let unreadNickName = document.querySelectorAll('.unreadNickName');

        for (i = 0; i <= chatRoom.length; i++) {
                unreadCount = unread[i].value;
                unreadName = unreadNickName[i].value;
                if ((unreadName == sender) && (unreadCount > 0)) {
                    chatRoom[i].style.display = "block";
                } else {
                    chatRoom[i].style.display = "none"; 
                }
            } 
    }
    
    
    
    
});

        // (지혜) 채팅창에서 프사,닉네임,책표지 클릭시 부모창 링크 변경(챗리스트에선 적용x)
        function changeParentPage(url) {
            parent = window.opener;
            parent.location.href=url;
            // parent.focus();  부모창으로 포커스(크롬에서는 지원되지 않는다 함...ㅠㅠ)

        }
        
        
    const usedBookId = document.querySelector('#usedBookId').value;
    const buyerId = document.querySelector('#chatWithId').value;
    const buyerName = document.querySelector('#chatWithName').value;
        
    // (지혜) 판매자가 '거래 예약' 클릭시
    function reserve() {
        
        console.log('usedBookId = '+usedBookId+', buyerId = '+buyerId+', buyerName = '+buyerName);
        const result = confirm(buyerName+'님과 거래 예약하시겠습니까?');

        const reserveDto = {
            usedBookId : usedBookId,
            userId : buyerId
        }
        
        if(result){
            axios.post('/chat/reserve', reserveDto)
             .then(response =>{
                 alert(buyerName +'님과 거래 예약되었습니다!');
                 console.log(response);
                 window.location.reload();    // 페이지 새로고침
             });
        };
    };
    
    
    // (지혜) 판매자가 '거래 취소' 클릭시
    function cancel() {
        
        console.log('usedBookId = '+usedBookId+', buyerName = '+buyerName);
        const result = confirm(buyerName+'님과의 예약을 취소하시겠습니까?');
        
        if(result){
            axios.post('/chat/cancel',null, { params: { usedBookId : usedBookId }})
             .then(response =>{
                 alert(buyerName +'님과의 거래가 취소되었습니다!');
                 console.log(response);
                 window.location.reload();    // 페이지 새로고침
             });
        };
    };
    
    
    
    // (지혜) 판매자가 '거래 완료' 클릭시
    function sold() {
        
        console.log('usedBookId = '+usedBookId+', buyerName = '+buyerName);
        const result = confirm(buyerName+'님과의 거래가 완료됐나요?');

        if(result){
            axios.post('/chat/sold', null, { params: { usedBookId : usedBookId }})
             .then(response =>{
                 alert(buyerName +'님과의 거래가 완료되었습니다!');
                 console.log(response);
                 window.location.reload();    // 페이지 새로고침
             })
             .catch(err => {
                console.log(err);
            });
        };
        
    };
    
    
    // (지혜) 검색칸에 입력하는 글자를 실시간으로 인식해서, 중고책 제목이 일치하는 채팅방 리스트만 보이도록 필터링
    function chatSearch() {
        
        let input = document.querySelector('#chatSearch');
        const filter = input.value.toUpperCase();   // 검색창에 입력되는 문자(대소문자 구별x)
        
        const btnChatSearch = document.querySelector('#btnChatSearch');
        
        if (filter == '') {
                btnChatSearch.style.display = "block";
            } else {
                btnChatSearch.style.display = "none";
            }
        
        let chatRoom = document.querySelectorAll('.btnChatRoom');
        for (i = 0; i <= chatRoom.length; i++) {
            console.log('챗방 갯수 = '+chatRoom.length+'개');
            let usedBookTitle = document.querySelectorAll('.usedBookTitle');
            if (usedBookTitle) {
                txtValue = usedBookTitle[i].value.toUpperCase();
                console.log('txtValue = '+txtValue);
                console.log('filter = '+filter);
                if (txtValue.indexOf(filter) > -1) {   // 검색창의 글자와 중고책 제목에 일치하는 글자가 있으면
                    chatRoom[i].style.display = "block";
                } else {
                    chatRoom[i].style.display = "none";
                }
            }
        }
    }