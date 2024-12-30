# QuickChat - Android Messaging App

QuickChat is a lightweight and secure Android messaging application that enables users to communicate seamlessly. The app provides essential features such as user registration, manual two-factor authentication (TFA), chat management, and an efficient search function.

---

## Features

- **User Registration & Authentication**  
  - Register a new account to get started.  
  - Log in with secure credentials.  
  - Reset your password if forgotten.  
  - Manually implemented Two-Factor Authentication (TFA) for added security.  

- **Messaging & Chat Management**  
  - Send and receive messages instantly.  
  - View all your inboxes for organized conversations.  
  - Delete chats to manage your conversation history.  

- **User Profile Management**  
  - Update your profile details to stay up to date.  

- **Search Functionality**  
  - Find specific users quickly to send messages.  

---

## Tech Stack

- **Programming Language**: Kotlin  
- **Database**: SQLite  
- **Authentication**: Manual Two-Factor Authentication (TFA) implementation using email-based OTP  
- **UI/UX**: Android Material Design  

---

## How to Install & Run the Project

1. **Clone the repository**:  
   ```bash
   git clone https://github.com/ermallimaj/quickchat.git
   cd QuickChat
   ```

2. **Open the project**:  
   Open the project in Android Studio.

3. **Set up Email Credentials**:  
   - Update the local.properties file with your email and app-specific password:
     EMAIL=<your-email>
     PASSWORD=<your-app-password>
   - Ensure SMTP is enabled on the email account for sending OTPs.

4. **Build and run**:  
   - Build the project in Android Studio.  
   - Deploy it on an emulator or a physical device.

---

## Contributors

- [Ardi Zariqi](https://github.com/ArdiZariqi)  
- [Ajshe Selmani](https://github.com/ajsheselmani)  
- [Ermal Limaj](https://github.com/ermallimaj)  
- [Erisa Cervadiku](https://github.com/erisa3002)  

---

## Feedback & Contribution

We’re always looking for ways to improve QuickChat! If you’d like to contribute, feel free to fork the repository and submit a pull request. For major changes, please open an issue first to discuss them.
