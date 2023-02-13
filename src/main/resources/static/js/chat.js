/**
 * 
 */
 
window.addEventListener('DOMContentLoaded', () => {
    
        var stompClient = null;
        var sender = $('#loginUser').val();
        var chatRoomId = $('#chatRoomId').val();
        var seller  = $('#seller').text();
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
        // 보내기 버튼 클릭시 실행되는 메서드
        const btnSend = document.querySelector('#btnSend');
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
        }
        
        
        // 메시지 입력 창에서 Enter키가 보내기와 연동되도록 설정
        var inputMessage = document.getElementById('message'); 
        inputMessage.addEventListener('keyup', function enterSend(event) {
            
            if (event.keyCode === null) {
                event.preventDefault();
            }
            
            if (event.keyCode === 13) {
                send();
            }
        });
        
        
        // (홍찬) 현재 시간 출력(채팅 주고 받을 때 보낸 시각, 받은 시각 출력)
        function getCurrentTime() {
        return new Date().toLocaleString();
        }
        
        // 입력한 메시지를 HTML 형태로 가공
        function createTextNode(messageObj) {
            if(messageObj.sender == sender){
                return '<p><div align="right" id="newHistory" class="row"><div class="col_8">' +
            messageObj.sender +
            '</div><div class="col_4 text-right">' +
            messageObj.message+
            '</div><div>[' +
            messageObj.sendTime +
            ']</div><span id="reads">1</span></p>';
            } else {
            return '<p><div id="newResponseHistory" class="row alert alert-info"><div class="col_8">' +
            messageObj.sender +
            '</div><div class="col_4 text-right">' +
            messageObj.message+
            '</div><div>[' +
            messageObj.sendTime +
            ']</div></p>';
            }
        }
        
        // 채팅 입력창에 포커싱 맞췄을 때 발동
        $('#message').focus(function(){
            const nm = document.querySelectorAll('#newResponseHistory');
            for (var i = 0; i < nm.length; ++i) {
            nm[i].className = "row";
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
                if(response == true){ // true: 상대가 online
                    return true;
                } else { // false: 상대가 offline
                    
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
});

        // (지혜) 채팅창에서 프사,닉네임,책표지 클릭시 부모창 링크 변경(챗리스트에선 적용x)
        function changeParentPage(url) {
            parent = window.opener;
            parent.location.href=url;
            // parent.focus();  부모창으로 포커스(크롬에서는 지원되지 않는다 함...ㅠㅠ)
        }
