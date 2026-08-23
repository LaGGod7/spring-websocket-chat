// 'use strict';
// const usernameForm = document.querySelector('#usernameForm');
// const usernameInput = document.querySelector('#name');
// const usernamePage =
//     document.querySelector('#usernamePage');
// const chatPage = document.querySelector('#chatPage');
// const users = document.querySelector('#users');
// const messageForm = document.querySelector('#messageForm');
// const messageInput = document.querySelector('#message');
//
// const messageArea = document.querySelector('#messageArea');
// let selectedUser = null;
// let username = null;
// let stompClient = null;
//
// function connect(event) {
//     event.preventDefault();
//     username = usernameInput.value.trim();
//     if (!username) return;
//     const socket = new SockJS("/ws");
//     stompClient = Stomp.over(socket);
//     stompClient.connect({username: username}, onConnected);
//
// }
//
// function onConnected() {
//     console.log("Connected to WebSocket!");
//     stompClient.subscribe('/topic/public', onMessageReceived);
//     stompClient.subscribe('/topic/users', onUsersReceived);
//     stompClient.subscribe('/user/queue/message', onPrivateMessage);
//     stompClient.send("/app/chat.addUser", {}, JSON.stringify({sender: username, messageType: 'JOIN'}))
//     usernameForm.style.display = 'none';
//     usernamePage.style.display = 'none';
//     chatPage.style.display = 'block';
//
// }
//
// function onPrivateMessage(payload) {
//
//     console.log("🔥 PRIVATE MESSAGE RECEIVED");
//
//     const message = JSON.parse(payload.body);
//
//     console.log(message);
// }
//
// function sendPrivateMessage() {
//     if (!selectedUser) {
//         console.log("Select a user first");
//         return;
//     }
//
//     const message = {
//         sender: username,
//         recipient: selectedUser,
//         content: "Hello Rahul!",
//         messageType: "CHAT"
//     };
//     if (!message.content) {
//         return;
//     }
//
//     stompClient.send(
//         "/app/chat.privateMessage",
//         {},
//         JSON.stringify(message)
//     );
//     messageInput.value = '';
// }
//
// function selectUser(user) {
//
//     selectedUser = user;
//
//     console.log("Selected user:", selectedUser);
//     console.log("PRIVATE CHAT WITH:", selectedUser);
// }
//
// function onUsersReceived(payload) {
//     const userList = JSON.parse(payload.body);
//     users.innerHTML = '';
//     userList.forEach(user => {
//         if (user === username) {
//             return;
//         }
//         const userElement = document.createElement('li');
//         userElement.textContent = user;
//         userElement.addEventListener(
//             'click',
//             () => selectUser(user)
//         );
//         users.appendChild(userElement);
//     })
//     console.log("ONLINE USERS:", users);
// }
//
// function onMessageReceived(payload) {
//
//     const chatMessage = JSON.parse(payload.body);
//
//     console.log(
//         "TYPE:", chatMessage.type,
//         "SENDER:", chatMessage.sender,
//         "CONTENT:", chatMessage.content
//     );
//
//     const messageElement = document.createElement('li');
//
//     if (chatMessage.messageType === "JOIN") {
//         messageElement.textContent =
//             chatMessage.sender + " joined the chat";
//
//     } else if (chatMessage.messageType === "LEAVE") {
//         messageElement.textContent =
//             chatMessage.sender + " left the chat";
//
//     } else if (chatMessage.messageType === "CHAT") {
//         messageElement.textContent =
//             chatMessage.sender + ": " +
//             chatMessage.content;
//     }
//
//     messageArea.appendChild(messageElement);
// }
//
// function sendMessage(event) {
//
//     event.preventDefault();
//     const messageContent = messageInput.value.trim();
//     if (!messageContent || !stompClient) return;
//     if (selectedUser) {
//
//         // PRIVATE MESSAGE
//
//         const chatMessage = {
//             sender: username,
//             recipient: selectedUser,
//             content: messageContent,
//             messageType: 'CHAT'
//         };
//
//         stompClient.send(
//             "/app/chat.privateMessage",
//             {},
//             JSON.stringify(chatMessage)
//         );
//
//     }
//     else {
//
//         // PUBLIC MESSAGE
//
//         const chatMessage = {
//             sender: username,
//             content: messageContent,
//             messageType: 'CHAT'
//         };
//
//         stompClient.send(
//             "/app/chat.sendMessage",
//             {},
//             JSON.stringify(chatMessage)
//         );
//     }
//     messageInput.value = '';
//
// }
//
//
// usernameForm.addEventListener(
//     'submit',
//     connect
// );
// messageForm.addEventListener(
//     'submit',
//     sendMessage
// );
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

let selectedUser = null;
let username = null;
let stompClient = null;

function connect(event) {
    event.preventDefault();

    username = usernameInput.value.trim();

    if (!username) return;

    const socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);

    stompClient.connect(
        { username: username },
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

    const messageElement = document.createElement('li');
    messageElement.classList.add('private-message');

    messageElement.textContent =
        message.sender + ": " + message.content;

    privateMessageArea.appendChild(messageElement);

    privateEmpty.style.display = 'none';

    // If we are already talking to this person, keep private tab open.
    if (message.sender === selectedUser) {
        showPrivateChat();
    }
}

function selectUser(user) {
    selectedUser = user;

    console.log("Selected user:", selectedUser);

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

function onUsersReceived(payload) {
    const userList = JSON.parse(payload.body);

    users.innerHTML = '';

    userList.forEach(user => {
        if (user === username) {
            return;
        }

        const userElement = document.createElement('li');

        userElement.textContent = user;

        if (user === selectedUser) {
            userElement.classList.add('selected');
        }

        userElement.addEventListener(
            'click',
            () => selectUser(user)
        );

        users.appendChild(userElement);
    });

    console.log("ONLINE USERS:", userList);
}

function onMessageReceived(payload) {
    const chatMessage = JSON.parse(payload.body);

    const messageElement = document.createElement('li');

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
            messageType: 'CHAT'
        };

        stompClient.send(
            "/app/chat.privateMessage",
            {},
            JSON.stringify(chatMessage)
        );

        // Show own private message immediately.
        const messageElement = document.createElement('li');
        messageElement.classList.add('private-message');
        messageElement.textContent =
            username + ": " + messageContent;

        privateMessageArea.appendChild(messageElement);
        privateEmpty.style.display = 'none';

    } else {
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

function showPublicChat() {
    publicChat.classList.add('active-view');
    privateChat.classList.remove('active-view');

    publicTab.classList.add('active');
    privateTab.classList.remove('active');

    chatTitle.textContent = "Public Chat";
    chatSubtitle.textContent = "Everyone can see these messages";
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
