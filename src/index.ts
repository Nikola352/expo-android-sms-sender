import { SimCard } from "./ExpoAndroidSmsSender.types";
import ExpoAndroidSmsSenderModule from "./ExpoAndroidSmsSenderModule";

export * from "./ExpoAndroidSmsSender.types";

/**
 * Retrieves a list of available SIM cards on the device.
 *
 * This function queries the device for active SIM cards and returns their details.
 * It requires the `READ_PHONE_STATE` permission to access SIM card information.
 * If the permission is not granted, the function will reject with an error.
 *
 * @returns A promise that resolves to an array of {@link SimCard} objects.
 *
 * @throws {Error} If permission is denied or there is a failure in retrieving SIM card info.
 */
export async function getSimCards(): Promise<SimCard[]> {
  const serialized = await ExpoAndroidSmsSenderModule.getSimCards();
  return JSON.parse(serialized);
}

/**
 * Sends an SMS message to a specified phone number.
 *
 * This function attempts to send an SMS using the system's SMS manager. It requires
 * the `SEND_SMS` permission to be granted by the user. If permission is not granted,
 * the function will reject with a `PERMISSION_DENIED` error.
 *
 * If `simCardId` is provided, the SMS will be sent using the specified SIM card.
 * If omitted, the system's default SIM card will be used. On devices with multiple SIMs,
 * this may prompt the user to choose a SIM if no default is set.
 *
 * ## Error Handling
 * - `PERMISSION_DENIED`: The required permissions were not granted.
 * - `INVALID_ARGUMENTS`: The phone number or message text is invalid.
 * - `NOT_SUPPORTED`: The device does not support sending SMS.
 * - Various system and carrier-related errors as specified in {@link https://developer.android.com/reference/android/telephony/SmsManager#sendTextMessage(java.lang.String,%20java.lang.String,%20java.lang.String,%20android.app.PendingIntent,%20android.app.PendingIntent) Android documentation}.
 *
 * @param phoneNumber The recipient's phone number.
 * @param text The message body to be sent.
 * @param simCardId (Optional) The ID of the SIM card to use, as retrieved by {@link getSimCards}.
 *
 * @returns A promise that resolves when the message is successfully sent.
 *          If the operation fails, the promise is rejected with an error.
 *
 * @throws {Error} If sending the SMS fails due to permissions, invalid input, or system errors.
 */
export async function sendSms(
  phoneNumber: string,
  text: string,
  simCardId?: number
): Promise<void> {
  await ExpoAndroidSmsSenderModule.sendSms(phoneNumber, text, simCardId);
}


export default { getSimCards, sendSms };
