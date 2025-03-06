import React, { useState } from "react";
import {
  PermissionsAndroid,
  SafeAreaView,
  Text,
  TouchableOpacity,
  View,
  Alert,
  Permission,
  StyleSheet,
} from "react-native";
import ExpoAndroidSmsSender, { SimCard } from "expo-android-sms-sender";

const PHONE_NUMBER = "REAL_PHONE_NUMBER";

/** Requests the specified Android permission. */
const requestPermission = async (permission: Permission) => {
  try {
    const granted = await PermissionsAndroid.request(permission);
    return granted === PermissionsAndroid.RESULTS.GRANTED;
  } catch (err) {
    console.warn(`Error requesting permission ${permission}:`, err);
    return false;
  }
};

export default function App() {
  const [simCards, setSimCards] = useState<SimCard[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  /** Handles retrieving SIM cards and sending SMS */
  const handleSendSms = async () => {
    setIsLoading(true);

    // Request SIM info permission
    const simPermission = await requestPermission(
      PermissionsAndroid.PERMISSIONS.READ_PHONE_STATE
    );
    if (!simPermission) {
      Alert.alert("Permission Denied", "Cannot access SIM card information.");
      setIsLoading(false);
      return;
    }

    try {
      const sims = await ExpoAndroidSmsSender.getSimCards();
      setSimCards(sims);
      console.log("Available SIM cards:", sims);
    } catch (error) {
      console.error("Failed to get SIM cards:", error);
      Alert.alert("Error", "Could not retrieve SIM cards.");
      setIsLoading(false);
      return;
    }

    // Request SMS permission
    const smsPermission = await requestPermission(
      PermissionsAndroid.PERMISSIONS.SEND_SMS
    );
    if (!smsPermission) {
      Alert.alert("Permission Denied", "Cannot send SMS.");
      setIsLoading(false);
      return;
    }

    try {
      // Sending SMS using the default SIM
      await ExpoAndroidSmsSender.sendSms(PHONE_NUMBER, "Hello!");

      // Sending SMS using a specific SIM if available
      if (simCards.length > 0) {
        await ExpoAndroidSmsSender.sendSms(
          PHONE_NUMBER,
          "Hello again!",
          simCards[0].id
        );
      }

      Alert.alert("Success", "Messages sent successfully!");
    } catch (error) {
      console.error("Failed to send SMS:", error);
      Alert.alert("Error", "Could not send SMS.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.content}>
        <Text style={styles.title}>Expo Android SMS Sender</Text>
        <TouchableOpacity
          style={styles.button}
          onPress={handleSendSms}
          disabled={isLoading}
        >
          <Text style={styles.buttonText}>
            {isLoading ? "Processing..." : "Send SMS"}
          </Text>
        </TouchableOpacity>
        {simCards.length > 0 && (
          <View style={styles.simInfo}>
            <Text style={styles.subtitle}>Detected SIM Cards:</Text>
            {simCards.map((sim, index) => (
              <Text
                key={sim.id}
              >{`SIM ${index + 1}: ${sim.carrierName} (${sim.displayName})`}</Text>
            ))}
          </View>
        )}
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#f8f9fa",
    alignItems: "center",
    justifyContent: "center",
  },
  content: {
    width: "80%",
    alignItems: "center",
  },
  title: {
    fontSize: 20,
    fontWeight: "bold",
    marginBottom: 20,
  },
  subtitle: {
    fontSize: 16,
    fontWeight: "600",
    marginTop: 10,
  },
  button: {
    backgroundColor: "#007bff",
    padding: 15,
    borderRadius: 8,
    alignItems: "center",
    marginTop: 10,
  },
  buttonText: {
    color: "#fff",
    fontSize: 16,
    fontWeight: "bold",
  },
  simInfo: {
    marginTop: 20,
    alignItems: "center",
  },
});
