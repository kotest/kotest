package io.kotest.framework.gradle.tasks

import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

// gradle requires the class be extendable
@CacheableTask // this allows gradle to cache our inputs
abstract class KotestJsTask @Inject internal constructor(
   private val executors: ExecOperations,
) : AbstractKotestTask() {

   // this is the name of the generated function from the compiler plugin
   // it should always match whatever the compiler plugin is using
   private val runKotestName = "main"

   // this is the name of the package where the compiler plugin places the generated top level run function
   // it should always match whatever the compiler plugin is using
   private val runKotestPackage = "io.kotest.runtime.js"

   @TaskAction
   protected fun execute() {

      // the kotlin js compiler uses projectname-test as the directory for test output, eg in build/js/packages
      val testModule = "${project.name}-test"

      println("Running kotest js tests $testModule")

      executors.exec {
         val buildDir = project.layout.buildDirectory.asFile.get().toPath()

         // this is the location where the kotlin js compiler puts all the test files after compilation
         // it would be good if we could derive this somehow other than assuming based on the project name
         val moduleFile = buildDir.resolve("js/packages/${testModule}/kotlin/${testModule}.js")

         val testFilter = if (tests.orNull == null) null else "'$tests'"

         // this is the entry point passed to node
         val nodeCommand = "require('${moduleFile}').$runKotestPackage.$runKotestName()"

         println("Node command :$nodeCommand")

         commandLine("node", "-e", nodeCommand)
      }
   }
}
