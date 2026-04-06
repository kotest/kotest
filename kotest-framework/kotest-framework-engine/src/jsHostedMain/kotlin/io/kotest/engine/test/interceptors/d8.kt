package io.kotest.engine.test.interceptors

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js

@OptIn(ExperimentalWasmJsInterop::class)
internal actual fun isD8Runtime(): Boolean = js("typeof d8 !== 'undefined'")
