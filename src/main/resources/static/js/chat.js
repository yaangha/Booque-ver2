/**
 * 
 */
 
window.addEventListener('DOMContentLoaded', () => {
   
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
            stompClient.subscribe("/topic/chat/"+ userName, function (response){
                let data = JSON.parse(response.body);
                 if (toChatUser === data.fromLogin) {
                render(data.message, data.fromLogin);
                } else {
                newMessages.set(data.fromLogin, data.message);
                }
            });
        });
    }
    
    // 기본 메세지 보내는 로직(보내는사람, 내용) stompClient(받는사람)
    function sendChat(from, text){
        stompClient.send("/app/chat/" + toChatUser, {}, JSON.stringify({
            fromLogin: from,
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
                alert("Login is already busy!")
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
            }
            
            $('#myList').html(usersTemplateHTML);
            
        });
        
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
});