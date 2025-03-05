import * as React from 'react';

import { ExpoAndroidSmsSenderViewProps } from './ExpoAndroidSmsSender.types';

export default function ExpoAndroidSmsSenderView(props: ExpoAndroidSmsSenderViewProps) {
  return (
    <div>
      <iframe
        style={{ flex: 1 }}
        src={props.url}
        onLoad={() => props.onLoad({ nativeEvent: { url: props.url } })}
      />
    </div>
  );
}
