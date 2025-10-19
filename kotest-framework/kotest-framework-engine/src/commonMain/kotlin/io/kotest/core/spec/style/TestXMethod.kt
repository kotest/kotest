package io.kotest.core.spec.style

enum class TestXMethod {
   NONE,
   FOCUSED, // if the test is explicitly focused, say through an annotation or method name
   DISABLED // if the test is explicitly disabled, say through an annotation or method name
}
