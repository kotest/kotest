package io.kotest.core

import io.kotest.fp.Option
import io.kotest.fp.toOption

actual fun sysprop(name: String): Option<String> = System.getProperty(name).toOption()
