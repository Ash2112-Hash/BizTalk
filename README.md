# Firebase-MessagingApp

This messaging application allows authorized users to connect with one another through messages, send E-transfer payments and customize their profiles to their suite. The pre-defined database contains necessary employee data to be cross-referenced during login and can be used in the context of a pre-existing organization which already stores employee information in a database.

## App Features
- **Messaging:** Exchange messages with other users in real time.
- **Mock Payments:** Send mock payments to other users within the app.
- **Profile Customization:** Customize your profile with profile pictures, usernames, and other details.
- **Firebase Cloud Messaging:** Uses FCM to intercept user messages, store their context and process forwarding to the recipient. 
- **Firebase FireStore:** cloud database connection to enable user data, payment info and messaging (encrypted through Kerberos) to be stored securely.

