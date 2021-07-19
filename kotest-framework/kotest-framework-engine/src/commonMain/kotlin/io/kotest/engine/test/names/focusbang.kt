package io.kotest.engine.test.names

import io.kotest.core.test.TestCase

/**
 * Returns true if this test is a focused test.
 * That is, if the name starts with "f:" and is a root test
 */
fun TestCase.isFocused() = this.descriptor.name.focus && this.descriptor.isTopLevel()

/**
 * Returns true if this test is disabled by being prefixed with a !
 */
fun TestCase.isBang(): Boolean = this.descriptor.name.bang
