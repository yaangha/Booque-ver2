/**
 * 
 */
 
window.addEventListener('DOMContentLoaded', () => {
   
   const url = 'http:/localhost:8888';
    let stompClient;
    let selectedUsers;
    
    function connectToChat(userName){
        let socket = new SockJS(url + '/chat');
        stompClient = Stomp.over(socket);
        stompClient.connect({},function(frame){
            console.log("connected to:"+ frame);
            stompClient.subscribe("/topic/chat"+ userName, function (response){
                let data = JSON.parse(response.body);
                console.log(data);
            });
        });
    }
    
    // TODO: Username, 메시지 보내기()
    function sendChat(from, text){
        stompClient.send("/app/chat/" + 2, {}, JSON.stringify({
            fromLogin: from,
            message: text
        }))
    };
   
    
    function registration(){
        let userName = document.queryElementById('userName').value;
        userName = 1;
        $.get(url + "/registration/" + userName, function (response) {
            connectToChat(userName);
        }).fail(function (error) {
            if(err.status == 400){
                alert("Login is already busy!")
            }
        })
    };

    // 유저 목록 새로고침
    function fetchAll(){
        $.get(url + "/fetchAllUsers", function(response) {
            let users = response;
            let usersTemplateHTML = "";
            for(let i=0; i<usersTemplateHTML.length; i++){
                usersTemplateHTML = usersTemplateHTML +
                            + '<a href="#" class="list-group-item list-group-item-action border-0">'
                            + '<div class="d-flex align-items-start">'
                            + '<img src="https://bootdey.com/img/Content/avatar/avatar3.png" class="rounded-circle mr-1"'
                            + 'alt="Jennifer Chang" width="40" height="40">'
                            + '<div class="name flex-grow-1 ml-3">' + users[i] + '<div class="small">'
                            + '<span class="fas fa-circle chat-offline"></span> Offline</div>'
                            + '</div>'
                            + '</div>'
                            + '</a>';
            }
        });
    }
    
    // 메세지 주고 받기
    
    let $chatHistory;
    let $button;
    let $textarea;
    let $chatHistoryList;
    
    function init() {
    cacheDOM();
     bindEvents();
    }
    
    function bindEvents() {
    $button.on('click', addChat.bind(this));
    }

    // data(보낼 메시지, 누구에게)
    function cacheDOM() {
    $chatHistory = $('.chat-history');
    $button = $('#btnSend');
    $textarea = $('#chat-to-send');
    $chatHistoryList = $chatHistory.find('ul');
    }
    
    // TODO: username
    function sendChatting(message) {
    let username = 2;
    console.log(username)
    sendChat(username, message);
    if (message.trim() !== '') {
        var context = {
            messageOutput: message,
            toUserName: selectedUser
        };

        $chatHistoryList.append(template(context));
        $textarea.val('');
    }
    }

    function addChat() {
        console.log($textarea.val());
    sendChatting($textarea.val());
    }
    
    function addChattingEnter(event) {
    // enter was pressed
    if (event.keyCode === 13) {
        addChat();
    }
    }
    
    init();
    
});