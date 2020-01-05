package io.kotest.core

/**
 * Describes whether a test is a Container or a Test using nomenclature from JUnit5.
 *
 * A [Container] is simply a branch. It can contain other tests.
 * A [Test] is simply a leaf. It cannot contain other tests.
 */
enum class TestType {
   Container, Test
}
