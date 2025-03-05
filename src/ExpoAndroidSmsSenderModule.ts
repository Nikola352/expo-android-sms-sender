import { NativeModule, requireNativeModule } from 'expo';

import { ExpoAndroidSmsSenderModuleEvents } from './ExpoAndroidSmsSender.types';

declare class ExpoAndroidSmsSenderModule extends NativeModule<ExpoAndroidSmsSenderModuleEvents> {
  PI: number;
  hello(): string;
  setValueAsync(value: string): Promise<void>;
}

// This call loads the native module object from the JSI.
export default requireNativeModule<ExpoAndroidSmsSenderModule>('ExpoAndroidSmsSender');
