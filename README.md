# SmsSenderApp

SmsSenderApp is a basic Android application that reads all SMS messages from your phone and forwards them to a specified email address. This app is especially useful for large teams of developers and testers who need to quickly receive messages with codes or other information from phones. This is a basic version of the app, and you can extend it to suit your needs as desired.

## Features

- Reads all incoming SMS messages.
- Forwards SMS messages to a specified email address.
- Runs in the background, ensuring you never miss an important message.
- Simple and intuitive UI for setting up the email address.

## How It Works

SmsSenderApp consists of two main components: `MainActivity` and `SmsEmailService`. Below is an overview of how these components work together to provide the app's functionality.

**`MainActivity`**

The main screen of the application. It handles the following tasks:

1. **Permission Handling**: Checks and requests necessary SMS permissions (`RECEIVE_SMS`, `READ_SMS`, and `SEND_SMS`).
2. **UI Interaction**: Allows the user to input and save the email address where SMS messages will be forwarded.
3. **Service Management**: Starts the `SmsEmailService` to listen for incoming SMS messages.

**`SmsEmailService`**

A background service that listens for incoming SMS messages and forwards them to the specified email address.

- **SMS Receiver**: A `BroadcastReceiver` listens for incoming SMS messages.
- **Email Sending**: When an SMS is received, it extracts the message content and sender, then sends this information to the specified email address using the RapidAPI Mail Sender API.

## Getting Started

Follow these steps to get started with SmsSenderApp:

1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/SmsSenderApp.git
   cd SmsSenderApp
2. Open the project in Android Studio.

3. Run the app on your Android device.

4. Enter your email address: Launch the app, enter the email address where you want to receive SMS messages, and click "Add Email".

5. Grant permissions: When prompted, grant the necessary SMS permissions.

## Usage
Once the app is running and permissions are granted, it will automatically forward all incoming SMS messages to the specified email address.

## Contributing
Contributions are welcome! Please open an issue or submit a pull request with any improvements or bug fixes.

## Contact
For any inquiries or support, please contact  korsun.dev@gmail.com.
