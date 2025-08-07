package io.kotest.framework.gradle

import org.gradle.process.ExecSpec
import org.gradle.process.internal.ExecAction

/**
 * This [NativeExecConfiguration] is responsible for configuring an [ExecAction] that will run tests
 * through the kotest engine by executing the debugTest executable created by the native compiler.
 */
internal data class NativeExecConfiguration(
   private val executable: String,
   private val tags: String? = null,
   private val descriptor: String? = null,
   private val moduleTestReportsDir: String? = null,
   private val rootTestReportsDir: String? = null,
   private val specs: List<String> = emptyList(),
) {

   companion object {
      // the value used to specify the team city format
      private const val LISTENER_TC = "teamcity"
   }

   fun withCommandLineTags(tags: String?): NativeExecConfiguration {
      return copy(tags = tags)
   }

   fun withModuleTestReportsDir(dir: String): NativeExecConfiguration {
      return copy(moduleTestReportsDir = dir)
   }

   fun withRootTestReportsDir(dir: String): NativeExecConfiguration {
      return copy(rootTestReportsDir = dir)
   }

   fun withDescriptor(descriptor: String?): NativeExecConfiguration {
      return copy(descriptor = descriptor)
   }

   fun configure(exec: ExecSpec) {
      exec.setCommandLine(executable)
      if (IntellijUtils.isIntellij())
         exec.environment("kotest.framework.runtime.native.listener", LISTENER_TC)
      if (descriptor != null)
         exec.environment("kotest.framework.runtime.native.include", descriptor)
      if (moduleTestReportsDir != null)
         exec.environment("kotest.framework.runtime.native.module.test.reports.dir", moduleTestReportsDir)
      if (rootTestReportsDir != null)
         exec.environment("kotest.framework.runtime.native.root.test.reports.dir", rootTestReportsDir)

      // this must be true so we can handle the failure ourselves by throwing GradleException
      // otherwise we get a nasty stack trace from gradle
      exec.isIgnoreExitValue = true
   }
}
