package io.kotlintest.extensions.system

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.extensions.TestListener
import java.util.concurrent.ConcurrentHashMap

/**
 * Will replace the [SecurityManager] used by the Java runtime
 * with a [NoExitSecurityManager] that will throw an exception
 * if System.exit() is invoked. This exception can then be
 * tested for with shouldThrow().
 *
 * After the spec has completed, the original security manager
 * will be set.
 *
 * To use this, override `listeners`() in your [Spec] class.
 *
 * Note: This listener is only suitable for use if parallelism is
 * set to 1 (the default) otherwise a race condition could occur.
 *
 * If you want to change the security manager for the entire
 * project, then use the alternative [SystemExitListener]
 */
object SpecSystemExitListener : TestListener {

  private val previousSecurityManagers = ConcurrentHashMap<Description, SecurityManager>()

  override fun beforeSpec(description: Description, spec: Spec) {
    val previous = System.getSecurityManager()
    if (previous != null)
      previousSecurityManagers[description] = previous
    System.setSecurityManager(NoExitSecurityManager(previous))
  }

  override fun afterSpec(description: Description, spec: Spec) {
    if (previousSecurityManagers.contains(description))
      System.setSecurityManager(previousSecurityManagers[description])
    else
      System.setSecurityManager(null)
  }
}

