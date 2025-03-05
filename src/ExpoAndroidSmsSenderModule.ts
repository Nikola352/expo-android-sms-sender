import { NativeModule, requireNativeModule } from 'expo';

declare class ExpoAndroidSmsSenderModule extends NativeModule {
  hello(): string;
}

// This call loads the native module object from the JSI.
export default requireNativeModule<ExpoAndroidSmsSenderModule>('ExpoAndroidSmsSender');
