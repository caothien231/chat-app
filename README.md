**Overview**
This project is a real-time chat application built with the following technologies:

Backend: Spring Boot, Kafka

Frontend: React.js

Messaging Protocol: WebSocket (STOMP over SockJS)

Database: MySQL (for user and message data persistence)

The application enables users to send and receive messages instantly, dynamically subscribing to Kafka topics for specific conversations.

************************************************************

**Features**
1. User Authentication

-  Users must log in to access the chat functionality.

2. Dynamic Topic Subscription

- New Kafka topics are created dynamically for each unique conversation (e.g., chat-1-2 for User 1 and User 2).

- Users are automatically subscribed to their conversation topics in real-time.

3. Real-Time Messaging

- Messages are sent and received via Kafka and delivered to users using WebSocket for instant updates.

4. Message History

- Fetch previous messages for ongoing conversations from the database.
  
**************************************************************

**Architecture**

**Backend**

1. Kafka:

Each chat conversation is mapped to a unique Kafka topic.

Kafka producers publish messages to topics.

Kafka consumers process and distribute messages.

2. WebSocket:

Facilitates real-time updates between the server and connected clients.

Clients receive notifications about new topics and messages.

3. Spring Boot:

Provides REST endpoints for sending and retrieving messages.

Integrates with Kafka to handle messaging logic.

**Frontend**

1.React.js:

User interface for the chat application.

WebSocket client for real-time communication.

2. STOMP over SockJS:

Enables WebSocket communication between the frontend and the server.
