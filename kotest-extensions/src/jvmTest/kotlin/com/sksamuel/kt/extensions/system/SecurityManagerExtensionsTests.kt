@file:Suppress("DEPRECATION")

package com.sksamuel.kt.extensions.system

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.extensions.system.SecurityManagerTestListener
import io.kotest.extensions.system.withSecurityManager
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.security.Permission

class SecurityManagerExtensionFunctionTests : StringSpec() {

  init {
    "Should reset the security manager after execution" {
      val originalSecurityManager = System.getSecurityManager()

      withSecurityManager(MySecurityManager) { }

      System.getSecurityManager() shouldBeSameInstanceAs originalSecurityManager
    }

    "Should reset the security manager even if there's an exception" {
      val originalSecurityManager = System.getSecurityManager()

      fun throwException(): Unit = throw RuntimeException() // Must be Unit. Nothing can't be used as reified

      try {
        withSecurityManager(MySecurityManager) { throwException() }
      } catch(_: Exception) {  }

      System.getSecurityManager() shouldBeSameInstanceAs originalSecurityManager
    }

    "Should use custom security manager during execution" {
      withSecurityManager(MySecurityManager) {
        System.getSecurityManager() shouldBe MySecurityManager
      }
    }

    "Should allow for suspend functions to be called" {

      @Suppress("RedundantSuspendModifier")
      suspend fun foo() {  }

      withSecurityManager(MySecurityManager) { foo() }
    }
  }
}

class SecurityManagerTestListenerTest : StringSpec() {

  init {
    "Should use custom security manager" {
      System.getSecurityManager() shouldBe MySecurityManager
    }
  }

  private var originalSecurityManager: SecurityManager? = null

  override suspend fun beforeTest(testCase: TestCase) {
    originalSecurityManager = System.getSecurityManager()
  }

  override suspend fun afterTest(testCase: TestCase, result: TestResult) {
    // Should reset to system default
    System.getSecurityManager() shouldBe originalSecurityManager
  }

  override val extensions = listOf(SecurityManagerTestListener(MySecurityManager))
}

private object MySecurityManager : SecurityManager() {
  override fun checkPermission(perm: Permission?) { /* Throw nothing */ }
}
