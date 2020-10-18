package io.kotest.extensions.livedata

import android.annotation.SuppressLint
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

@SuppressLint("RestrictedApi")
class ImmediateTaskExecutorListener: TestListener {
   init {
      initDelegate()
   }

   override suspend fun beforeTest(testCase: TestCase) {
      super.beforeTest(testCase)
      initDelegate()
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      super.afterTest(testCase, result)
      ArchTaskExecutor.getInstance().setDelegate(null)
   }

   private fun initDelegate() {
      ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
         override fun executeOnDiskIO(runnable: Runnable) {
            runnable.run()
         }

         override fun postToMainThread(runnable: Runnable) {
            runnable.run()
         }

         override fun isMainThread(): Boolean {
            return true
         }
      })
   }
}
