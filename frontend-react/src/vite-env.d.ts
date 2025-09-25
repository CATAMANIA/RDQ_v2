/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_APP_VERSION?: string
  readonly VITE_APP_BUILD_DATE?: string
  readonly VITE_APP_DEBUG_STATE?: string
  readonly MODE: string
  readonly BASE_URL: string
  readonly PROD: boolean
  readonly DEV: boolean
  readonly SSR: boolean
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}