package expo.modules.androidsmssender

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import java.net.URL

class ExpoAndroidSmsSenderModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("ExpoAndroidSmsSender")

    Function("hello") {
      "Hello world! 👋"
    }
  }
}
