package io.kotest.framework.gradle.tasks

import io.kotest.framework.gradle.IntellijUtils
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.nio.file.Files
import javax.inject.Inject

// gradle requires the class be extendable
@CacheableTask // this allows gradle to cache our inputs
abstract class KotestWasmTask @Inject internal constructor(
   private val executors: ExecOperations,
) : AbstractKotestTask() {

   companion object {

      // this is the name of the generated function from the KSP plugin
      // it should always match whatever the plugin is generating
      private const val KOTEST_RUN_FN_NAME = "runKotest"

      // the value used to specify the team city format
      private const val LISTENER_TC = "teamcity"

      private const val LISTENER_CONSOLE = "console"
   }

   @get:Input
   abstract val nodeExecutable: Property<String>

   @get:Input
   abstract val compileSyncPath: Property<String>

   @TaskAction
   protected fun execute() {
      executors.exec {
         val descriptorArg = if (descriptor.orNull == null) null else "'${descriptor.get()}'"
         val listenerArg = if (IntellijUtils.isIntellij()) LISTENER_TC else LISTENER_CONSOLE
         val testReportsDirArg = testReportsDir.get().asFile.absolutePath

         // must be .mjs to support modules
         val file = Files.createTempFile("runKotest", ".mjs")
         file.toFile().deleteOnExit()
         Files.writeString(
            file,
            // this is the entry point passed to node which references the well defined runKotest function
            // this differs from JS in that require() is not supported in wasm, so we use ECMAScript modules
            """
import * as exports from '${compileSyncPath.get()}';
exports["$KOTEST_RUN_FN_NAME"]('$listenerArg', $descriptorArg, '$testReportsDirArg');
"""
         )
         commandLine(nodeExecutable.get(), file.toFile().absolutePath)
      }
   }
}

