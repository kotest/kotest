package io.kotest.extensions.system

import io.kotest.TestCase
import io.kotest.TestResult
import io.kotest.extensions.TestListener

/**
 * Replaces System Security Manager with [securityManager]
 *
 * This function replaces the System Security Manager with the specified [securityManager], executes [block] and then
 * returns the original manager to its place. This will also happen if the original manager is null.
 *
 * **Attention**: This code is subject to race conditions. As Java's System Manager is only one per JVM, this code
 * will replace it. However, all other tests are going to use it if tests are running in parallel.
 */
inline fun <reified T> withSecurityManager(securityManager: SecurityManager?, block: () -> T): T {
  val originalSecurityManager = System.getSecurityManager()
  
  System.setSecurityManager(securityManager)
  
  try {
    return block()
  } finally {
    System.setSecurityManager(originalSecurityManager)
  }
}


abstract class SecurityManagerListener(
        protected val securityManager: SecurityManager?
) : TestListener {
  
  private val originalSecurityManager = System.getSecurityManager()
  
  protected fun changeSecurityManager() {
    System.setSecurityManager(securityManager)
  }
  
  protected fun resetSecurityManager() {
    System.setSecurityManager(originalSecurityManager)
  }
}

/**
 * Overrides System Security Manager with specified [securityManager]
 *
 * This is a Listener for code that uses the System Security Manager. It replaces the System Security Manager with the
 * desired [securityManager].
 *
 * After the execution of the test, the System Security Manager is set to what it was before. This will also happen if
 * the original manager is null.
 *
 * **Attention**: This code is subject to race conditions. As Java's System Manager is only one per JVM, this code
 * will replace it. However, all other tests are going to use it if tests are running in parallel.
 */
class SecurityManagerTestListener(
        securityManager: SecurityManager?
) : SecurityManagerListener(securityManager) {
  
  override fun beforeTest(testCase: TestCase) {
    changeSecurityManager()
  }
  
  override fun afterTest(testCase: TestCase, result: TestResult) {
    resetSecurityManager()
  }
}

/**
 * Overrides System Security Manager with specified [securityManager]
 *
 * This is a Listener for code that uses the System Security Manager. It replaces the System Security Manager with the
 * desired [securityManager].
 *
 * After the execution of the project, the System Security Manager is set to what it was before. This will also happen if
 * the original manager is null.
 *
 * **Attention**: This code is subject to race conditions. As Java's System Manager is only one per JVM, this code
 * will replace it. However, all other tests are going to use it if tests are running in parallel.
 */
class SecurityManagerProjectListener(
        securityManager: SecurityManager?
) : SecurityManagerListener(securityManager) {
  
  override fun beforeProject() {
    changeSecurityManager()
  }
  
  override fun afterProject() {
    resetSecurityManager()
  }
}
