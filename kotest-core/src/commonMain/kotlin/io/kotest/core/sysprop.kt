package io.kotest.core

import io.kotest.fp.Option

expect fun sysprop(name: String): Option<String>
