package io.kotest.framework.gradle.tasks

import io.kotest.framework.gradle.SpecsResolver
import io.kotest.framework.gradle.TestLauncherArgsJavaExecConfiguration
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.options.Option

/**
 * When you use the standard "Debug" action on a Gradle task (one that implements JavaForkOptions, like Test or JavaExec), IntelliJ IDEA:
 * Recognizes that it's a debuggable Gradle task.
 * IntelliJ then automatically sets up and connects a debugger to the forked JVM process, mirroring the behavior it has for standard tests.
 */
// this allows gradle to cache our inputs
@CacheableTask
// gradle requires the class be open or abstract to be able to subclass it
abstract class KotestJvmTask : JavaExec() {

   @get:Option(option = "specs", description = "The specs list to avoid scanning")
   @get:Input
   @get:Optional
   abstract val specs: Property<String>

   @get:Option(option = "packages", description = "Specify the packages to limit after scanning")
   @get:Input
   @get:Optional
   abstract val packages: Property<String>

   // this is the sourceset that contains the tests
   // this will be usally "test" for JVM projects and "jvmTest" for multiplatform projects
   @get:Input
   abstract val testSourceSetClasspath: Property<FileCollection>

   @get:Input
   @get:Optional
   abstract val include: Property<String>

   @get:Input
   @get:Optional
   abstract val moduleName: Property<String>

   @get:InputFiles
   @get:PathSensitive(PathSensitivity.RELATIVE)
   @get:Optional
   abstract val moduleTestReportsDir: DirectoryProperty

   @get:InputFiles
   @get:PathSensitive(PathSensitivity.RELATIVE)
   @get:Optional
   abstract val rootTestReportsDir: DirectoryProperty

   override fun exec() {
      val specs = SpecsResolver.specs(specs, packages, testSourceSetClasspath.get())
      if (specs.isEmpty()) {
         println("> No tests found for module ${moduleName.get()}")
      } else {

         // builds the arg string at runtime
         TestLauncherArgsJavaExecConfiguration()
            .withSpecs(specs)
            .withInclude(include.orNull)
            .withModuleTestReportsDir(moduleTestReportsDir.get().asFile.absolutePath)
            .withRootTestReportsDir(rootTestReportsDir.get().asFile.absolutePath)
//            .withCommandLineTags(tags.orNull)
            .configure(this)

         println(args.joinToString(" "))
         super.exec()

         val result = executionResult.get()
         if (result.exitValue != 0) {
            throw GradleException("Test suite failed with errors")
         }
      }
   }
}
