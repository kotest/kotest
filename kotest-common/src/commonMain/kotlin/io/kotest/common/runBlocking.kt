package io.kotest.common

expect fun <T> runBlocking(f: suspend () -> T): T
