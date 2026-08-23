'use strict';
const usernameForm = document.querySelector('#usernameForm');
const usernameInput = document.querySelector('#name');
const usernamePage =
    document.querySelector('#usernamePage');
const chatPage = document.querySelector('#chatPage');
const users = document.querySelector('#users');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');

const messageArea = document.querySelector('#messageArea');
let selectedUser = null;
let username = null;
let stompClient = null;

function connect(event) {
    event.preventDefault();
    username = usernameInput.value.trim();
    if (!username) return;
    const socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({username: username}, onConnected);

}

function onConnected() {
    console.log("Connected to WebSocket!");
    stompClient.subscribe('/topic/public', onMessageReceived);
    stompClient.subscribe('/topic/users', onUsersReceived);
    stompClient.subscribe('/user/queue/message', onPrivateMessage);
    stompClient.send("/app/chat.addUser", {}, JSON.stringify({sender: username, messageType: 'JOIN'}))
    usernameForm.style.display = 'none';
    usernamePage.style.display = 'none';
    chatPage.style.display = 'block';

}

function onPrivateMessage(payload) {

    console.log("🔥 PRIVATE MESSAGE RECEIVED");

    const message = JSON.parse(payload.body);

    console.log(message);
}

function sendPrivateMessage() {
    if (!selectedUser) {
        console.log("Select a user first");
        return;
    }

    const message = {
        sender: username,
        recipient: selectedUser,
        content: "Hello Rahul!",
        messageType: "CHAT"
    };
    if (!message.content) {
        return;
    }

    stompClient.send(
        "/app/chat.privateMessage",
        {},
        JSON.stringify(message)
    );
    messageInput.value = '';
}

function selectUser(user) {

    selectedUser = user;

    console.log("Selected user:", selectedUser);
    console.log("PRIVATE CHAT WITH:", selectedUser);
}

function onUsersReceived(payload) {
    const userList = JSON.parse(payload.body);
    users.innerHTML = '';
    userList.forEach(user => {
        if (user === username) {
            return;
        }
        const userElement = document.createElement('li');
        userElement.textContent = user;
        userElement.addEventListener(
            'click',
            () => selectUser(user)
        );
        users.appendChild(userElement);
    })
    console.log("ONLINE USERS:", users);
}

function onMessageReceived(payload) {

    const chatMessage = JSON.parse(payload.body);

    console.log(
        "TYPE:", chatMessage.type,
        "SENDER:", chatMessage.sender,
        "CONTENT:", chatMessage.content
    );

    const messageElement = document.createElement('li');

    if (chatMessage.messageType === "JOIN") {
        messageElement.textContent =
            chatMessage.sender + " joined the chat";

    } else if (chatMessage.messageType === "LEAVE") {
        messageElement.textContent =
            chatMessage.sender + " left the chat";

    } else if (chatMessage.messageType === "CHAT") {
        messageElement.textContent =
            chatMessage.sender + ": " +
            chatMessage.content;
    }

    messageArea.appendChild(messageElement);
}

function sendMessage(event) {

    event.preventDefault();
    const messageContent = messageInput.value.trim();
    if (!messageContent || !stompClient) return;
    if (selectedUser) {

        // PRIVATE MESSAGE

        const chatMessage = {
            sender: username,
            recipient: selectedUser,
            content: messageContent,
            messageType: 'CHAT'
        };

        stompClient.send(
            "/app/chat.privateMessage",
            {},
            JSON.stringify(chatMessage)
        );

    }
    else {

        // PUBLIC MESSAGE

        const chatMessage = {
            sender: username,
            content: messageContent,
            messageType: 'CHAT'
        };

        stompClient.send(
            "/app/chat.sendMessage",
            {},
            JSON.stringify(chatMessage)
        );
    }
    messageInput.value = '';

}


usernameForm.addEventListener(
    'submit',
    connect
);
messageForm.addEventListener(
    'submit',
    sendMessage
);