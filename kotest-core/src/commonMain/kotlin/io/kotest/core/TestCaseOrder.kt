package io.kotest.core

enum class TestCaseOrder {
   // the order in which the tests are defined in code
   Sequential,
   // a randomized order
   Random,
   // ordered a-z
   Lexicographic
}
