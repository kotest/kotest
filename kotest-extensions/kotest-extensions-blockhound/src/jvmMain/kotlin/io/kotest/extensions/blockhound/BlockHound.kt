package io.kotest.extensions.blockhound

import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

enum class BlockHoundMode {
   DISABLED,
   ERROR,
   PRINT
}

data class BlockHound(private val mode: BlockHoundMode = BlockHoundMode.ERROR) : TestCaseExtension {
   override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
      // Skip intercepting lower level tests in case of nesting.
      if (testCase.parent != null) return execute(testCase)

      initialize()

      require (activeExtension == null) {
         "${testCase.name.testName}: Cannot register $this, as $activeExtension is already active"
      }
      activeExtension = this

      val modeBefore = activeMode
      val testCaseBefore = activeTestCase
      activeMode = mode
      activeTestCase = testCase

      try {
         return execute(testCase)
      } finally {
         activeMode = modeBefore
         activeTestCase = testCaseBefore

         activeExtension = null
      }
   }

   companion object {
      private var isInitialized = false
      private var activeExtension: BlockHound? = null
      internal var activeMode = BlockHoundMode.DISABLED
      internal var activeTestCase: TestCase? = null

      private fun initialize() {
         if (!isInitialized) {
            reactor.blockhound.BlockHound.install()
            isInitialized = true
         }
      }
   }
}
