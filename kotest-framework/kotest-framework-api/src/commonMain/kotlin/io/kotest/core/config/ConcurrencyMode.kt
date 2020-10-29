package io.kotest.core.config

enum class ConcurrencyMode {
   None, // all tests/specs operate sequentially
   Spec, // all specs are launched concurrently
   Test, // all tests in a spec are launched concurrently, but specs are launched sequentially
   All, // both specs and tests are launched concurrently
}
