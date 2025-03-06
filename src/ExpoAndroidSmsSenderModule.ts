import { NativeModule, requireNativeModule } from 'expo';

declare class ExpoAndroidSmsSenderModule extends NativeModule {
  getSimCards(): Promise<string>;
  sendSms(phoneNumber: string, text: string, simCardId?: number): Promise<void>;
}

// This call loads the native module object from the JSI.
export default requireNativeModule<ExpoAndroidSmsSenderModule>('ExpoAndroidSmsSender');
