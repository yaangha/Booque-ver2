/**
 * 
 */
 
window.addEventListener('DOMContentLoaded', () => {
<<<<<<< HEAD
    
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
=======
   
   const url = 'http:/localhost:8888';
    let stompClient;
    let toChatUser = document.querySelector('#startChatWithYou').value;
    let newMessages = new Map();
    
    // 웹소켓 연결해주는 fn/ topic: 받는 사람 app: 보내는사람
    function connectToChat(userName){
        let socket = new SockJS(url + '/chat');
        stompClient = Stomp.over(socket);
        stompClient.connect({},function(frame){
            console.log("connected to:"+ frame);
            stompClient.subscribe("/topic/messages/"+ userName, function (response){
                let data = JSON.parse(response.body);
                 if (toChatUser == data.sender) {
                render(data.message, data.sender);
                } else {
                newMessages.set(data.sender, data.message);
                }
>>>>>>> branch '0210_YeJin' of https://github.com/yaangha/Booque-ver2.git
            });
<<<<<<< HEAD
 
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
            
            const finalChat = document.querySelectorAll('#finalChat')
            
            console.log('메세지 보자 재정렬를 해야함. ')
            console.log(finalChat)
            console.log(message)

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
=======
        });
    }
    
    // 기본 메세지 보내는 로직(보내는사람, 내용) stompClient(받는사람)
    function sendChat(from, text){
        stompClient.send("/app/chat/" + toChatUser, {}, JSON.stringify({
            sender: from,
            message: text
        }))
    };
   
    // 웹소켓 연결(대화할 대상) TODO
    // const btnRegistration = document.querySelector('#startChatWithYou');
    // btnRegistration.addEventListener('click', registration);
    
    // 대화를 시작하는 대상 등록
    function registration(){
        let userName = document.getElementById('loginUser').value;
        $.get("/registration/" + userName, function (response) {
            connectToChat(userName);
        }).fail(function (error) {
            if(error.status == 400){
                alert("이미 연결된 채팅방이 있습니다.")
            }
        })
    };
    

    // 내가 대화중인 채팅방 목록 갱신
    const btnRefresh = document.querySelector('#refresh');
    btnRefresh.addEventListener('click', fetchAll);
    
    function fetchAll(){
        $.get("/fetchAllUsers", function(response) {
            let users = response;
            let usersTemplateHTML = "";
            for(let i=0; i<users.length; i++){
                usersTemplateHTML += '<a href="#" onclick="selectUser(\''+ users[i]+'\')" class="list-group-item list-group-item-action border-0">'
                            + '<li class="clearfix">'
                            + '<div class="badge bg-success float-right">2</div>'
                            + '<div class="d-flex align-items-start">'
                            + '<img src="https://bootdey.com/img/Content/avatar/avatar3.png" class="rounded-circle mr-1"'
                            + 'alt="Jennifer Chang" width="40" height="40">'
                            + '<div class="name flex-grow-1 ml-3">' + users[i] + '<div class="small">'
                            + '<span class="fas fa-circle chat-online"></span> Online</div>'
                            + '</div>'
                            + '</div>'
                            + '</li>'
                            + '</a>';
>>>>>>> branch '0210_YeJin' of https://github.com/yaangha/Booque-ver2.git
            }
            
            $('#myList').html(usersTemplateHTML);
            
        });
        
<<<<<<< HEAD
        
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
        
=======
    }
    
    
    
    
    
    // 메세지 주고 받기 관련 시작
    let $chatHistory;
    let $button;
    let $textarea;
    let $chatHistoryList;
    
    
    function init() {
    cacheDOM();
     bindEvents();
    }
    
    // 이벤트
    function bindEvents() {
    $button.on('click', addChat.bind(this)); // 나머지 이벤트는 클릭 이벤트
     $textarea.on('keyup', addChattingEnter.bind(this)); // 엔터키로 전송 가능하도록 하기 위해서
    }

    // data(보낼 메시지, 누구에게)
    function cacheDOM() {
    $chatHistory = $('.chat-history');
    $button = $('#btnSend');
    $textarea = $('#chat-to-send');
    $chatHistoryList = $chatHistory.find('ul');
    }
    
    
    // 메세지 보내는 함수
    // 메세지 보내주고 화면에 띄워주고 입력칸 비워주고 하는 것.
    function sendChatting(message) {
    let loginUser = document.getElementById('loginUser').value;
    console.log(loginUser)
    sendChat(loginUser, message);
    autoScroll();
    if (message.trim() !== '') { // 문자열 좌우측 공백을 제거 한다는데..? 말그대로 채팅 좌우 공백 없애주는듯
         var template = Handlebars.compile($("#message-template").html());
        var context = {
            messageOutput: message,
            time: getCurrentTime(),
            toUserName: toChatUser
        };
        $chatHistoryList.append(template(context));
        autoScroll();
        $textarea.val('');
    }
    }


    // 화면에 주고 받은 메시지 ㅍ보여주기 
    function render(message, userName) {
        autoScroll();
    // responses
    var templateResponse = Handlebars.compile($("#message-response-template").html());
    var contextResponse = {
        response: message,
        time: getCurrentTime(),
        userName: userName
    };
    setTimeout(function () {
        $chatHistoryList.append(templateResponse(contextResponse));
        autoScroll();
    }.bind(this), 1500);
    }
    
    
    function addChat() {
        console.log($textarea.val());
    sendChatting($textarea.val());
    }
    
    // 채팅할 때 엔터키로 전송 허용
    function addChattingEnter(event) {
    if (event.keyCode === 13) {
        addChat();
    }
    }
    
    // 채팅칠 때 혹은 채팅받을 때 자동으로 스크롤 조절 해줌.
    function autoScroll() {
    $chatHistory.scrollTop($chatHistory[0].scrollHeight);
    }

    
    // 현재 시간 출력(채팅 주고 받을 때 보낸 시각, 받은 시각 출력)
    function getCurrentTime() {
    return new Date().toLocaleTimeString().replace(/([\d]+:[\d]{2})(:[\d]{2})(.*)/, "$1$3");
    }
    
    init();
    registration();
    connectToChat(toChatUser);
>>>>>>> branch '0210_YeJin' of https://github.com/yaangha/Booque-ver2.git
});