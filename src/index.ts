// Reexport the native module. On web, it will be resolved to ExpoAndroidSmsSenderModule.web.ts
// and on native platforms to ExpoAndroidSmsSenderModule.ts
export { default } from './ExpoAndroidSmsSenderModule';
export { default as ExpoAndroidSmsSenderView } from './ExpoAndroidSmsSenderView';
export * from  './ExpoAndroidSmsSender.types';
