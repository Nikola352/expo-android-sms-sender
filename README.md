# 📩 Expo Android SMS Sender

**Expo Android SMS Sender** is a lightweight, easy-to-use library for sending SMS messages and retrieving SIM card information in React Native applications using the [Expo Modules API](https://docs.expo.dev/modules/). This package is designed specifically for **Android** devices.

❗ **This library only works on Android.** Sending SMS **programmatically** is not possible on iOS due to Apple's security restrictions. If you need to **open the messaging app** on iOS instead, consider using [`expo-sms`](https://docs.expo.dev/versions/latest/sdk/sms/).

---

## 🚀 Features

- Send SMS messages directly from your React Native app.
- Retrieve available SIM cards on dual-SIM devices.
- Select a specific SIM card for sending messages.
- Built with **Expo Modules API** for better native integration.

---

## 📦 Installation

This package requires a **bare Expo project** with a **development build** because it uses native code. Follow these steps to install:

### 1️⃣ Install the package:

```sh
npm install expo-android-sms-sender
```

### 2️⃣ Rebuild your project
Since this package includes native code, you must rebuild your app using a **development build**:
```sh
eas build --profile development --platform android
```
or if using Expo Dev Client:
```sh
npx expo run:android
```

---

## 🔒 Permissions
- The `SEND_SMS` permission is required for the `sendSms()` function.
- The `READ_PHONE_STATE` permission is required for the `getSimCards()` function.

Make sure to request these permissions at runtime before using the respective functions.

---

## 📖 API Reference

### `getSimCards(): Promise<SimCard[]>`
Retrieves information about available SIM cards on the device.

#### Returns:

An array of objects with the following properties:
```ts
type SimCard = {
  id: number;          // Unique SIM card identifier (subscription ID)
  displayName: string; // SIM name as displayed in system settings
  carrierName: string; // Mobile network carrier name
  slotIndex?: number;  // Slot index (if available)
};
```

#### Example usage:
```ts
import { getSimCards } from "expo-android-sms-sender";
import { PermissionsAndroid } from 'react-native';

async function fetchSimCards() {
  try {
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.READ_PHONE_STATE
    );
    if (granted === PermissionsAndroid.RESULTS.GRANTED) {
      const sims = await getSimCards();
      console.log("Available SIM Cards:", sims);
    } else {
      console.log("READ_PHONE_STATE permission denied");
    }
  } catch (error) {
    console.error("Error fetching SIM cards:", error);
  }
}
```

### `sendSms(phoneNumber: string, text: string, simCardId?: number): Promise<void>`

#### Parameters:
- `phoneNumber` (string) → The recipient's phone number.
- `text` (string) → The message body.
- `simCardId` (number, optional) → The ID of the SIM card to use. If omitted, the system will use the default SIM.

#### Example usage:
```ts
import { sendSms } from "expo-android-sms-sender";
import { PermissionsAndroid } from 'react-native';

async function sendMessage() {
  try {
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.SEND_SMS
    );
    if (granted === PermissionsAndroid.RESULTS.GRANTED) {
      await sendSms("PHONE_NUMBER_TO_SEND_TO", "Hello from Expo!");
      console.log("Message sent successfully!");
    } else {
      console.log("SEND_SMS permission denied");
    }
  } catch (error) {
    console.error("Error sending SMS:", error);
  }
}
```

---

## 📢 Reporting Issues & Feature Requests

If you encounter a bug, have a question, or want to request a feature, please [open an issue](https://github.com/Nikola352/expo-android-sms-sender/issues) on GitHub.

When submitting an issue, please include:
- A clear description of the problem.
- Steps to reproduce it.
- Any relevant logs or error messages.
- The version of the library and your Expo SDK version.

We appreciate your feedback and contributions!

---

## 🤝 Contributing

We welcome contributions from the community! If you'd like to contribute:

1. Fork the repository on GitHub.
2. Create a new branch (feature/my-feature or fix/bug-name).
3. Commit your changes with clear messages.
4. Submit a pull request and wait for review.

Make sure to follow the project's coding guidelines.

## 📝 License

MIT license. Feel free to use and modify.