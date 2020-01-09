package io.kotest.core

import io.kotest.fp.Option

actual fun sysprop(name: String): Option<String> = Option.None
