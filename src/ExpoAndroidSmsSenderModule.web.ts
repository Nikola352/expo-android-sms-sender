import { registerWebModule, NativeModule } from 'expo';

import { ExpoAndroidSmsSenderModuleEvents } from './ExpoAndroidSmsSender.types';

class ExpoAndroidSmsSenderModule extends NativeModule<ExpoAndroidSmsSenderModuleEvents> {
  PI = Math.PI;
  async setValueAsync(value: string): Promise<void> {
    this.emit('onChange', { value });
  }
  hello() {
    return 'Hello world! 👋';
  }
}

export default registerWebModule(ExpoAndroidSmsSenderModule);
