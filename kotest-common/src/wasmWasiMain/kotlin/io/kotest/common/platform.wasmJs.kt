package io.kotest.common

actual val platformExecution = object : PlatformEnvironment {
   override val platform: Platform = Platform.WasmJs
}
