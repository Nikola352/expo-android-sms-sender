import { requireNativeView } from 'expo';
import * as React from 'react';

import { ExpoAndroidSmsSenderViewProps } from './ExpoAndroidSmsSender.types';

const NativeView: React.ComponentType<ExpoAndroidSmsSenderViewProps> =
  requireNativeView('ExpoAndroidSmsSender');

export default function ExpoAndroidSmsSenderView(props: ExpoAndroidSmsSenderViewProps) {
  return <NativeView {...props} />;
}
