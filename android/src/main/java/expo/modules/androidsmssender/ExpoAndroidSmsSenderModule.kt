package expo.modules.androidsmssender

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import androidx.core.content.ContextCompat
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import expo.modules.kotlin.Promise
import com.google.gson.Gson

class ExpoAndroidSmsSenderModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("ExpoAndroidSmsSender")

    AsyncFunction("getSimCards") { promise: Promise ->
      val context = appContext.reactContext ?: return@AsyncFunction

      if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
        promise.reject(PERMISSION_DENIED, "Permission not granted to access SIM card info.", null)
        return@AsyncFunction
      }

      try {
        val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        val activeSubscriptions = subscriptionManager.activeSubscriptionInfoList

        val simCards = activeSubscriptions?.map { info ->
          SimCard(
            info.subscriptionId,
            info.displayName.toString(),
            info.carrierName.toString(),
            if (info.simSlotIndex == SubscriptionManager.INVALID_SIM_SLOT_INDEX) null else info.simSlotIndex
          )
        } ?: emptyList()

        promise.resolve(Gson().toJson(simCards))
      } catch (e: Exception) {
        promise.reject(GENERIC_ERROR, "Failed to retrieve SIM card info: ${e.message}", e)
      }
    }

    AsyncFunction("sendSms") { phoneNumber: String, message: String, simCardId: Int?, promise: Promise ->
      val context = appContext.reactContext ?: return@AsyncFunction

      if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
        promise.reject(PERMISSION_DENIED, "Permission not granted to send SMS.", null)
        return@AsyncFunction
      }

      val smsManager = getSmsManager(context, simCardId)
      val sentIntent = createSentPendingIntent(context, promise)
      
      try {
        smsManager.sendTextMessage(phoneNumber, null, message, sentIntent, null)
      } catch(e: IllegalArgumentException) {
        promise.reject("INVALID_ARGUMENTS", "Invalid arguments provided", e)
      } catch(e: UnsupportedOperationException) {
        promise.reject("NOT_SUPPORTED", "Sending SMS is not supported on your device", e)
      }
    }
  }

  private fun getSmsManager(context: Context, simCardId: Int?): SmsManager {
    return if (simCardId == null) {
      context.getSystemService(SmsManager::class.java)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      context.getSystemService(SmsManager::class.java).createForSubscriptionId(simCardId)
    } else {
      SmsManager.getSmsManagerForSubscriptionId(simCardId)
    }
  }

  private fun createSentPendingIntent(context: Context, promise: Promise): PendingIntent {
    val intent = Intent(SMS_SENT_ACTION).setPackage(context.packageName)
    val pendingIntent = PendingIntent.getBroadcast(
      context,
      System.currentTimeMillis().toInt(),
      intent,
      PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
    )

    ContextCompat.registerReceiver(context, object : BroadcastReceiver() {
      override fun onReceive(ctx: Context?, intent: Intent?) {
        context.unregisterReceiver(this)
        
        val errorCode = SMS_ERROR_CODES[resultCode] ?: "UNKNOWN_ERROR"
        val errorMessage = SMS_ERROR_DESCRIPTIONS[errorCode] ?: "Unknown error occurred with result code: $resultCode"
        
        if (resultCode == Activity.RESULT_OK) {
          promise.resolve(null)
        } else {
          promise.reject(errorCode, errorMessage, null)
        }
      }
    }, IntentFilter(SMS_SENT_ACTION), ContextCompat.RECEIVER_NOT_EXPORTED)

    return pendingIntent
  }

    companion object {
    private const val PERMISSION_DENIED = "PERMISSION_DENIED"
    private const val GENERIC_ERROR = "ERROR"
    
    private val SMS_ERROR_CODES = mapOf(
      SmsManager.RESULT_ERROR_GENERIC_FAILURE to "GENERIC_FAILURE",
      SmsManager.RESULT_ERROR_NO_SERVICE to "NO_SERVICE",
      SmsManager.RESULT_ERROR_NULL_PDU to "NULL_PDU",
      SmsManager.RESULT_ERROR_RADIO_OFF to "RADIO_OFF",
      SmsManager.RESULT_ERROR_LIMIT_EXCEEDED to "LIMIT_EXCEEDED",
      SmsManager.RESULT_ERROR_FDN_CHECK_FAILURE to "FDN_CHECK_FAILURE",
      SmsManager.RESULT_ERROR_SHORT_CODE_NOT_ALLOWED to "SHORT_CODE_NOT_ALLOWED",
      SmsManager.RESULT_ERROR_SHORT_CODE_NEVER_ALLOWED to "SHORT_CODE_NEVER_ALLOWED",
      SmsManager.RESULT_RADIO_NOT_AVAILABLE to "RADIO_NOT_AVAILABLE",
      SmsManager.RESULT_NETWORK_REJECT to "NETWORK_REJECT",
      SmsManager.RESULT_INVALID_ARGUMENTS to "INVALID_ARGUMENTS",
      SmsManager.RESULT_INVALID_STATE to "INVALID_STATE",
      SmsManager.RESULT_NO_MEMORY to "NO_MEMORY",
      SmsManager.RESULT_INVALID_SMS_FORMAT to "INVALID_SMS_FORMAT",
      SmsManager.RESULT_SYSTEM_ERROR to "SYSTEM_ERROR",
      SmsManager.RESULT_MODEM_ERROR to "MODEM_ERROR",
      SmsManager.RESULT_NETWORK_ERROR to "NETWORK_ERROR",
      SmsManager.RESULT_ENCODING_ERROR to "ENCODING_ERROR",
      SmsManager.RESULT_INVALID_SMSC_ADDRESS to "INVALID_SMSC_ADDRESS",
      SmsManager.RESULT_OPERATION_NOT_ALLOWED to "OPERATION_NOT_ALLOWED",
      SmsManager.RESULT_INTERNAL_ERROR to "INTERNAL_ERROR",
      SmsManager.RESULT_NO_RESOURCES to "NO_RESOURCES",
      SmsManager.RESULT_CANCELLED to "CANCELLED",
      SmsManager.RESULT_REQUEST_NOT_SUPPORTED to "REQUEST_NOT_SUPPORTED",
      SmsManager.RESULT_NO_BLUETOOTH_SERVICE to "NO_BLUETOOTH_SERVICE",
      SmsManager.RESULT_INVALID_BLUETOOTH_ADDRESS to "INVALID_BLUETOOTH_ADDRESS",
      SmsManager.RESULT_BLUETOOTH_DISCONNECTED to "BLUETOOTH_DISCONNECTED",
      SmsManager.RESULT_UNEXPECTED_EVENT_STOP_SENDING to "UNEXPECTED_EVENT_STOP_SENDING",
      SmsManager.RESULT_SMS_BLOCKED_DURING_EMERGENCY to "SMS_BLOCKED_DURING_EMERGENCY",
      SmsManager.RESULT_SMS_SEND_RETRY_FAILED to "SMS_SEND_RETRY_FAILED",
      SmsManager.RESULT_REMOTE_EXCEPTION to "REMOTE_EXCEPTION",
      SmsManager.RESULT_NO_DEFAULT_SMS_APP to "NO_DEFAULT_SMS_APP",
      SmsManager.RESULT_RIL_RADIO_NOT_AVAILABLE to "RIL_RADIO_NOT_AVAILABLE",
      SmsManager.RESULT_RIL_SMS_SEND_FAIL_RETRY to "RIL_SMS_SEND_FAIL_RETRY",
      SmsManager.RESULT_RIL_NETWORK_REJECT to "RIL_NETWORK_REJECT",
      SmsManager.RESULT_RIL_INVALID_STATE to "RIL_INVALID_STATE",
      SmsManager.RESULT_RIL_INVALID_ARGUMENTS to "RIL_INVALID_ARGUMENTS",
      SmsManager.RESULT_RIL_NO_MEMORY to "RIL_NO_MEMORY",
      SmsManager.RESULT_RIL_REQUEST_RATE_LIMITED to "RIL_REQUEST_RATE_LIMITED",
      SmsManager.RESULT_RIL_INVALID_SMS_FORMAT to "RIL_INVALID_SMS_FORMAT",
      SmsManager.RESULT_RIL_SYSTEM_ERR to "RIL_SYSTEM_ERR",
      SmsManager.RESULT_RIL_ENCODING_ERR to "RIL_ENCODING_ERR",
      SmsManager.RESULT_RIL_INVALID_SMSC_ADDRESS to "RIL_INVALID_SMSC_ADDRESS",
      SmsManager.RESULT_RIL_MODEM_ERR to "RIL_MODEM_ERR",
      SmsManager.RESULT_RIL_NETWORK_ERR to "RIL_NETWORK_ERR",
      SmsManager.RESULT_RIL_INTERNAL_ERR to "RIL_INTERNAL_ERR",
      SmsManager.RESULT_RIL_REQUEST_NOT_SUPPORTED to "RIL_REQUEST_NOT_SUPPORTED",
      SmsManager.RESULT_RIL_INVALID_MODEM_STATE to "RIL_INVALID_MODEM_STATE",
      SmsManager.RESULT_RIL_NETWORK_NOT_READY to "RIL_NETWORK_NOT_READY",
      SmsManager.RESULT_RIL_OPERATION_NOT_ALLOWED to "RIL_OPERATION_NOT_ALLOWED",
      SmsManager.RESULT_RIL_NO_RESOURCES to "RIL_NO_RESOURCES",
      SmsManager.RESULT_RIL_CANCELLED to "RIL_CANCELLED",
      SmsManager.RESULT_RIL_SIM_ABSENT to "RIL_SIM_ABSENT",
      SmsManager.RESULT_RIL_SIMULTANEOUS_SMS_AND_CALL_NOT_ALLOWED to "RIL_SIMULTANEOUS_SMS_AND_CALL_NOT_ALLOWED",
      SmsManager.RESULT_RIL_ACCESS_BARRED to "RIL_ACCESS_BARRED",
      SmsManager.RESULT_RIL_BLOCKED_DUE_TO_CALL to "RIL_BLOCKED_DUE_TO_CALL"
    )

    private val SMS_ERROR_DESCRIPTIONS = mapOf(
      "GENERIC_FAILURE" to "Generic failure",
      "NO_SERVICE" to "No service available",
      "NULL_PDU" to "Null PDU",
      "RADIO_OFF" to "Radio off",
      "LIMIT_EXCEEDED" to "SMS sending limit exceeded",
      "FDN_CHECK_FAILURE" to "Fixed dialing number (FDN) check failed",
      "SHORT_CODE_NOT_ALLOWED" to "Short code not allowed",
      "SHORT_CODE_NEVER_ALLOWED" to "Short code never allowed",
      "RADIO_NOT_AVAILABLE" to "Radio not available",
      "NETWORK_REJECT" to "Network rejected the SMS",
      "INVALID_ARGUMENTS" to "Invalid arguments provided",
      "INVALID_STATE" to "Invalid state",
      "NO_MEMORY" to "No memory available",
      "INVALID_SMS_FORMAT" to "Invalid SMS format",
      "SYSTEM_ERROR" to "System error",
      "MODEM_ERROR" to "Modem error",
      "NETWORK_ERROR" to "Network error",
      "ENCODING_ERROR" to "Encoding error",
      "INVALID_SMSC_ADDRESS" to "Invalid SMSC address",
      "OPERATION_NOT_ALLOWED" to "Operation not allowed",
      "INTERNAL_ERROR" to "Internal error",
      "NO_RESOURCES" to "No resources available",
      "CANCELLED" to "SMS sending cancelled",
      "REQUEST_NOT_SUPPORTED" to "Request not supported",
      "NO_BLUETOOTH_SERVICE" to "No Bluetooth service available",
      "INVALID_BLUETOOTH_ADDRESS" to "Invalid Bluetooth address",
      "BLUETOOTH_DISCONNECTED" to "Bluetooth disconnected",
      "UNEXPECTED_EVENT_STOP_SENDING" to "Unexpected event stopped SMS sending",
      "SMS_BLOCKED_DURING_EMERGENCY" to "SMS blocked during emergency",
      "SMS_SEND_RETRY_FAILED" to "SMS send retry failed",
      "REMOTE_EXCEPTION" to "Remote exception occurred",
      "NO_DEFAULT_SMS_APP" to "No default SMS app",
      "RIL_RADIO_NOT_AVAILABLE" to "RIL radio not available",
      "RIL_SMS_SEND_FAIL_RETRY" to "RIL SMS send failed, retry",
      "RIL_NETWORK_REJECT" to "RIL network rejected the SMS",
      "RIL_INVALID_STATE" to "RIL invalid state",
      "RIL_INVALID_ARGUMENTS" to "RIL invalid arguments",
      "RIL_NO_MEMORY" to "RIL no memory available",
      "RIL_REQUEST_RATE_LIMITED" to "RIL request rate limited",
      "RIL_INVALID_SMS_FORMAT" to "RIL invalid SMS format",
      "RIL_SYSTEM_ERR" to "RIL system error",
      "RIL_ENCODING_ERR" to "RIL encoding error",
      "RIL_INVALID_SMSC_ADDRESS" to "RIL invalid SMSC address",
      "RIL_MODEM_ERR" to "RIL modem error",
      "RIL_NETWORK_ERR" to "RIL network error",
      "RIL_INTERNAL_ERR" to "RIL internal error",
      "RIL_REQUEST_NOT_SUPPORTED" to "RIL request not supported",
      "RIL_INVALID_MODEM_STATE" to "RIL invalid modem state",
      "RIL_NETWORK_NOT_READY" to "RIL network not ready",
      "RIL_OPERATION_NOT_ALLOWED" to "RIL operation not allowed",
      "RIL_NO_RESOURCES" to "RIL no resources available",
      "RIL_CANCELLED" to "RIL SMS sending cancelled",
      "RIL_SIM_ABSENT" to "RIL SIM absent",
      "RIL_SIMULTANEOUS_SMS_AND_CALL_NOT_ALLOWED" to "RIL simultaneous SMS and call not allowed",
      "RIL_ACCESS_BARRED" to "RIL access barred",
      "RIL_BLOCKED_DUE_TO_CALL" to "RIL SMS blocked due to call",
      "UNKNOWN_ERROR" to "Unknown error occurred"
    )
    
    private const val SMS_SENT_ACTION = "SMS_SENT"
  }
}
