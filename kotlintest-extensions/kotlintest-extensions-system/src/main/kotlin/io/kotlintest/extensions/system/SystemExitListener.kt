package io.kotlintest.extensions.system

import io.kotlintest.AbstractProjectConfig
import io.kotlintest.Spec
import io.kotlintest.extensions.TestListener

/**
 * Will replace the [SecurityManager] used by the Java runtime
 * with a [NoExitSecurityManager] that will throw an exception
 * if System.exit() is invoked. This exception can then be
 * tested for with shouldThrow().
 *
 * To use this, override `listeners()` in your [AbstractProjectConfig].
 *
 * Note: This listener will change the security manager
 * for all tests. If you want to change the security manager
 * for just a single [Spec] then consider the
 * alternative [SpecSystemExitListener]
 */
object SystemExitListener : TestListener {
  override fun beforeProject() {
    val previous = System.getSecurityManager()
    System.setSecurityManager(NoExitSecurityManager(previous))
  }
}