package io.kotest.mpp

actual fun jsProcessEnv(name: String): String? = js("process.env[name]") as String?
