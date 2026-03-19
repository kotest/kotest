package io.kotest.plugin.intellij.run.idea

import com.intellij.execution.JavaTestFrameworkDebuggerRunner
import com.intellij.execution.configurations.RunProfile
import io.kotest.plugin.intellij.Constants

@Deprecated("Starting with Kotest 6 the preferred method is to run via gradle")
class KotestDebuggerRunner : JavaTestFrameworkDebuggerRunner() {

   override fun validForProfile(profile: RunProfile): Boolean {
      return profile is KotestRunConfiguration
   }

   override fun getThreadName(): String = Constants.FRAMEWORK_NAME
   override fun getRunnerId(): String = "KotestDebug"
}
