
'use strict';

const usernameForm = document.querySelector('#usernameForm');
const usernameInput = document.querySelector('#name');
const usernamePage = document.querySelector('#usernamePage');
const chatPage = document.querySelector('#chatPage');

const users = document.querySelector('#users');

const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');

const messageArea = document.querySelector('#messageArea');
const privateMessageArea = document.querySelector('#privateMessageArea');
const privateEmpty = document.querySelector('#privateEmpty');

const publicChat = document.querySelector('#publicChat');
const privateChat = document.querySelector('#privateChat');

const publicTab = document.querySelector('#publicTab');
const privateTab = document.querySelector('#privateTab');

const chatTitle = document.querySelector('#chatTitle');
const chatSubtitle = document.querySelector('#chatSubtitle');
const passwordInput =
    document.querySelector("#password");
let selectedUser = null;
let username = null;
let stompClient = null;
let onlineUsers = [];
let jwt = null;
const conversations ={};


async function connect(event) {
    event.preventDefault();

    username = usernameInput.value.trim();
    const password = passwordInput.value.trim();
    if (!username || !password) return;
    const response = await fetch("/auth/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            username: username,
            password: password
        })
    });
    if (!response.ok) {
        alert("Invalid username or password");
        return;
    }
    jwt = await response.text();

    const socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);

    stompClient.connect(
        {
            Authorization: "Bearer " + jwt
        },
        onConnected
    );
}


function onConnected() {
    console.log("Connected to WebSocket!");

    stompClient.subscribe(
        '/topic/public',
        onMessageReceived
    );

    stompClient.subscribe(
        '/topic/users',
        onUsersReceived
    );

    stompClient.subscribe(
        '/user/queue/message',
        onPrivateMessage
    );

    stompClient.send(
        "/app/chat.addUser",
        {},
        JSON.stringify({
            sender: username,
            messageType: 'JOIN'
        })
    );

    usernameForm.style.display = 'none';
    usernamePage.style.display = 'none';
    chatPage.style.display = 'grid';
}

function onPrivateMessage(payload) {
    const message = JSON.parse(payload.body);

    console.log("PRIVATE MESSAGE:", message);
    // 1. Always store it
    storePrivateMessage(message);

    // 2. Find which conversation it belongs to
    const conversationUser =
        message.sender === username
            ? message.recipient
            : message.sender;


        // 3. Only render if we're looking at that conversation
        if (conversationUser === selectedUser) {
            renderConversations(selectedUser);
            privateEmpty.style.display = 'none';
            showPrivateChat();
        }


}

function selectUser(user) {
    selectedUser = user;

    console.log("Selected user:", selectedUser);
    if (conversations[user]) {
        conversations[user].unread = 0;
    }
    renderUsers();
    loadConversation(user);

    document.querySelectorAll('#users li').forEach(element => {
        element.classList.remove('selected');

        if (element.textContent === user) {
            element.classList.add('selected');
        }
    });

    showPrivateChat();

    chatTitle.textContent = "Chat with " + user;
    chatSubtitle.textContent = "Private conversation";
}
function renderUsers(userList) {
    users.innerHTML = '';

    onlineUsers.forEach(user => {
        if (user === username) {
            return;
        }

        const userElement = document.createElement('li');

        const nameElement = document.createElement('span');
        nameElement.textContent = user;

        userElement.appendChild(nameElement);

        const conversation = conversations[user];

        if (conversation && conversation.unread > 0) {
            const badge = document.createElement('span');

            badge.textContent = conversation.unread;
            badge.classList.add('unread-badge');

            userElement.appendChild(badge);
        }

        if (user === selectedUser) {
            userElement.classList.add('selected');
        }

        userElement.addEventListener(
            'click',
            () => selectUser(user)
        );

        users.appendChild(userElement);
    });
}

function onUsersReceived(payload) {
    const userList = JSON.parse(payload.body);

    onlineUsers = userList;

    renderUsers();

    console.log("ONLINE USERS:", userList);
}

function onMessageReceived(payload) {
    const chatMessage = JSON.parse(payload.body);

    const messageElement = document.createElement('li');
    messageElement.classList.add('public-message');

    if (chatMessage.messageType === "JOIN") {
        messageElement.textContent =
            chatMessage.sender + " joined the chat";

        messageElement.classList.add('system-message');

    } else if (chatMessage.messageType === "LEAVE") {
        messageElement.textContent =
            chatMessage.sender + " left the chat";

        messageElement.classList.add('system-message');

    } else if (chatMessage.messageType === "CHAT") {
        messageElement.textContent =
            chatMessage.sender + ": " +
            chatMessage.content;
        const timestampElement =
            document.createElement('small');

        timestampElement.textContent =
            formatTimestamp(chatMessage.timestamp);
        messageElement.appendChild(timestampElement);
    }


    messageArea.appendChild(messageElement);
}

function sendMessage(event) {
    event.preventDefault();

    const messageContent = messageInput.value.trim();

    if (!messageContent || !stompClient) {
        return;
    }

    if (selectedUser) {
        const chatMessage = {
            sender: username,
            recipient: selectedUser,
            content: messageContent,
            messageType: 'CHAT',
            timestamp: new Date().toISOString()
        };

        stompClient.send(
            "/app/chat.privateMessage",
            {},
            JSON.stringify(chatMessage)
        );
        privateEmpty.style.display = 'none';

        privateEmpty.style.display = 'none';
        // // Show own private message immediately.
        // const messageElement = document.createElement('li');
        // messageElement.classList.add('private-message');
        // messageElement.textContent =
        //     username + ": " + messageContent;
        //
        // privateMessageArea.appendChild(messageElement);
        // privateEmpty.style.display = 'none';

    } else {
        const chatMessage = {
            sender: username,
            content: messageContent,
            messageType: 'CHAT',
            timestamp: new Date().toISOString()
        };

        stompClient.send(
            "/app/chat.sendMessage",
            {},
            JSON.stringify(chatMessage)
        );
    }

    messageInput.value = '';
}
function formatTimestamp(timestamp) {

    const date = new Date(timestamp);

    return date.toLocaleTimeString([], {
        hour: '2-digit',
        minute: '2-digit'
    });
}
async function loadConversation(user) {

    const response = await fetch(`/messages/${user}`, {
        headers: {
            Authorization: "Bearer " + jwt
        }
    });
    if (!response.ok) {
        console.log("Failed to load conversation:", response.status);
        return;
    }
    const messages =await response.json();
    conversations[user]={
        messages:messages,
        unread:0
    };
    privateMessageArea.innerHTML = '';
    // const messages = conversations[user]?.messages || [];
    messages.forEach(message => {

        const messageElement =
            document.createElement('li');
        messageElement.classList.add('private-message');


        messageElement.textContent =
            message.sender + ": " +
            message.content;
        const timestampElement =
            document.createElement('small');
        timestampElement.textContent =
            formatTimestamp(message.timestamp);
        messageElement.appendChild(timestampElement);
        privateMessageArea.appendChild(messageElement);
    });
}
function renderConversations(user){
    privateMessageArea.innerHTML ="";
    const messages = conversations[user]?.messages||[];
    messages.forEach(message=>{
        const messageElement = document.createElement('li');
        messageElement.classList.add("private-message");
        messageElement.textContent=message.sender+": "+message.content;
        const timestampElement =
            document.createElement('small');

        timestampElement.textContent =
            formatTimestamp(message.timestamp);

        messageElement.appendChild(timestampElement);

        privateMessageArea.appendChild(messageElement);

    })
}

function showPublicChat() {
    selectedUser = null;
    publicChat.classList.add('active-view');
    privateChat.classList.remove('active-view');

    publicTab.classList.add('active');
    privateTab.classList.remove('active');

    chatTitle.textContent = "Public Chat";
    chatSubtitle.textContent = "Everyone can see these messages";
    renderUsers();
}

function showPrivateChat() {
    publicChat.classList.remove('active-view');
    privateChat.classList.add('active-view');

    publicTab.classList.remove('active');
    privateTab.classList.add('active');

    if (selectedUser) {
        chatTitle.textContent = "Chat with " + selectedUser;
        chatSubtitle.textContent = "Private conversation";
    }
}
function storePrivateMessage(message) {

    const conversationUser = message.sender === username?message.recipient:message.sender;
    if (!conversations[conversationUser]) {
        conversations[conversationUser] = {
            messages: [],
            unread: 0
        };
    }

    conversations[conversationUser].messages.push(message);
    if (conversationUser !== selectedUser) {
        conversations[conversationUser].unread++;
    }
    renderUsers();
    console.log("STORED FOR:", conversationUser);
    console.log("ALL CONVERSATIONS:", conversations);
}

publicTab.addEventListener('click', showPublicChat);

privateTab.addEventListener('click', () => {
    if (selectedUser) {
        showPrivateChat();
    } else {
        console.log("Select a user first");
    }
});

usernameForm.addEventListener(
    'submit',
    connect
);

messageForm.addEventListener(
    'submit',
    sendMessage
);
