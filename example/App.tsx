import { SafeAreaView, Text, TouchableOpacity } from "react-native";
import ExpoAndroidSmsSender from "expo-android-sms-sender";

export default function App() {
  const handleClick = () => {
    const hello = ExpoAndroidSmsSender.hello();
    console.log(hello);
  };

  return (
    <SafeAreaView style={styles.container}>
      <TouchableOpacity onPress={handleClick}>
        <Text>Log hello</Text>
      </TouchableOpacity>
    </SafeAreaView>
  );
}

const styles = {
  container: {
    flex: 1,
    backgroundColor: "#eee",
  },
};
