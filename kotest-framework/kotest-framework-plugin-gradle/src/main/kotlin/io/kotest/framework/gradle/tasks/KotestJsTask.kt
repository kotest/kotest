package io.kotest.framework.gradle.tasks

import io.kotest.framework.gradle.IntellijUtils
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

// gradle requires the class be extendable
@CacheableTask // this allows gradle to cache our inputs
abstract class KotestJsTask @Inject internal constructor(
   private val executors: ExecOperations,
) : AbstractKotestTask() {

   companion object {

      // this is the name of the generated function from the KSP plugin
      // it should always match whatever the plugin is generating
      private const val KOTEST_RUN_FN_NAME = "runKotest"

      // this is the name of the package where the KSP plugin places the generated top level run function
      // it should always match whatever the plugin is generating
      private const val KOTEST_JS_GENERATED_PACKAGE = "io.kotest.framework.runtime.js"

      // the value used to specify the team city format
      private const val LISTENER_TC = "teamcity"
      private const val LISTENER_CONSOLE = "console"
   }

   @get:Input
   abstract val nodeExecutable: Property<String>

   @TaskAction
   protected fun execute() {
      executors.exec {
         println("specs: ${specs.getOrElse("")}")
         println("Node executable ${nodeExecutable.get()}")

         // the kotlin js compiler uses projectname-test as the module name, eg in build/js/packages
         val testModuleName = "${project.name}-test"
         println("JS Test Module $testModuleName")

         val buildDir = project.layout.buildDirectory.asFile.get().toPath()

         // this is the location where the kotlin js compiler puts all the test files after compilation
         // it would be good if we could derive this somehow other than assuming based on the project name
         val moduleFile = buildDir.resolve("js/packages/${testModuleName}/kotlin/${testModuleName}.js")

         val descriptorArg = if (descriptor.orNull == null) null else "'${descriptor.get()}'"
         val listenerArg = if (IntellijUtils.isIntellij()) LISTENER_TC else LISTENER_CONSOLE

         // this is the entry point passed to node which references the well defined runKotest function
         val nodeCommand =
            "require('${moduleFile}').$KOTEST_JS_GENERATED_PACKAGE.$KOTEST_RUN_FN_NAME('$listenerArg', $descriptorArg)"
         println("Node command :$nodeCommand")

         commandLine(nodeExecutable.get(), "-e", nodeCommand)
      }
   }
}

