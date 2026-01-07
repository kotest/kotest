package io.kotest.plugin

import io.kotest.core.spec.style.funSpec
import io.kotest.core.spec.style.stringSpec

fun factory(a: String, b: String) = stringSpec {
}

fun factoryWithArgs(a: String, b: String) = funSpec {
}
