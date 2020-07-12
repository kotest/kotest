package io.kotest.plugin.intellij

import com.intellij.execution.JavaTestFrameworkDebuggerRunner
import com.intellij.execution.configurations.RunProfile

class KotestDebuggerRunner : JavaTestFrameworkDebuggerRunner() {

   override fun validForProfile(profile: RunProfile): Boolean {
      return profile is KotestConfiguration
   }

   override fun getThreadName(): String = "kotest"
   override fun getRunnerId(): String = "KotestDebug"
}
