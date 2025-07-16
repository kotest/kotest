package at.asitplus.test

fun detectEnvironment(): String {
    return when {
        js("typeof window !== 'undefined'") as Boolean -> "Browser"
        js("typeof global !== 'undefined'") as Boolean -> "Node"
        js("typeof self !== 'undefined'") as Boolean -> "WebWorker"
        else -> "unknown"
    }
}
