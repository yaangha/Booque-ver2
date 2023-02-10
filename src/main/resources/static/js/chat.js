/**
 * 
 */
 
window.addEventListener('DOMContentLoaded', () => {
    
        var stompClient = null;
        var sender = $('#loginUser').val();
        var chatRoomId = $('#chatRoomId').val();
        // invoke when DOM(Documents Object Model; HTML(<head>, <body>...etc) is ready
        $(document).ready(connect());
        
        function connect() {
            // map URL using SockJS 
            var socket = new SockJS('/chat');
            var url = '/user/' + chatRoomId + '/queue/messages';
            // webSocket 대신 SockJS을 사용하므로 Stomp.client()가 아닌 Stomp.over()를 사용함
            stompClient = Stomp.over(socket);
            // connect(header, connectCallback(==연결에 성공하면 실행되는 메서드))
            stompClient.connect({}, function() {
                autoScroll();
                // url: 채팅방 참여자들에게 공유되는 경로
                // callback(function()): 클라이언트가 서버(Controller broker)로부터 메시지를 수신했을 때(== STOMP send()가 실행되었을 때) 실행
                stompClient.subscribe(url, function(output) {
                    // JSP <body>에 append할 메시지 contents
                    showBroadcastMessage(createTextNode(JSON.parse(output.body)));
                    autoScroll();
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
            ']</div><span id="check">1</span></p>';
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
        
        
        $('#message').focus(function(){
            
            let nm = document.getElementById('newResponseHistory');
            nm.className = "row";
            nm.removeAttribute('id');
            if(sender == sender){
                console.log("확인해주세요!")
        setInterval( CheckPageFocus, 200 );
        }
        });
        
        function CheckPageFocus() {
        //var info = document.getElementById("message");
        if ( document.hasFocus() ) {
            let nm = document.getElementById('check');
            nm.style.visibility = 'hidden';
            nm.removeAttribute('id');
         } 
        }
        
        
        // (홍찬) 채팅칠 때 혹은 채팅받을 때 자동으로 스크롤 조절 해줌.
        function autoScroll() {
        $chatHistory = $('#content');
        $chatHistory.scrollTop($chatHistory[0].scrollHeight);
        }
        
        // (홍찬) 메세지 보낼때/받을 때 대화목록 refresh
        //$('.input_group').on('focusin',function(){
        //   $('.chat-history').load(location.href+' .chat-history');
        //});
        
        // HTML 형태의 메시지를 화면에 출력해줌
        // 해당되는 id 태그의 모든 하위 내용들을 message가 추가된 내용으로 갱신해줌
        function showBroadcastMessage(message) {
            $("#content").html($("#content").html() + message);
        }
        
});