package io.kotest.framework.gradle.tasks

import io.kotest.framework.gradle.SpecsResolver
import io.kotest.framework.gradle.TestLauncherArgsJavaExecConfiguration
import org.gradle.api.attributes.Attribute
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.options.Option

// gradle requires the class be extendable
@CacheableTask // this allows gradle to cache our inputs
abstract class KotestAndroidTask : JavaExec() {

   companion object {
      // unsure why this is needed, but without it the resolver complains about too many candidates for AGP plugin
      val ARTIFACT_TYPE = Attribute.of("artifactType", String::class.java)

      // artifactTypes published by an Android library
      const val TYPE_CLASSES_JAR = "android-classes-jar"; // In AAR
      const val TYPE_CLASSES_DIR = "android-classes-directory"; // Not in AAR
   }

   @get:Option(option = "include", description = "Filter to a single spec or test")
   @get:Input
   @get:Optional
   abstract val include: Property<String>

   @get:Option(option = "specs", description = "The specs list to avoid scanning")
   @get:Input
   @get:Optional
   abstract val specs: Property<String>

   @get:Option(option = "packages", description = "Specify the packages to limit after scanning")
   @get:Input
   @get:Optional
   abstract val packages: Property<String>

   @get:Internal
   abstract val moduleTestReportsDir: DirectoryProperty

   @get:Internal
   abstract val rootTestReportsDir: DirectoryProperty

   /**
    * The classpath to scan for tests, should just be the output of the compilation
    */
   @get:Input
   abstract val specsClasspath: Property<FileCollection>

   @get:Input
   abstract val compilationName: Property<String>

   @get:Input
   @get:Optional
   abstract val targetName: Property<String>

   override fun exec() {

      val specs = SpecsResolver.specs(specs, packages, specsClasspath.get())
      if (specs.isEmpty()) {
         println("> No tests found for module ${compilationName.get()}")
      } else {
         TestLauncherArgsJavaExecConfiguration()
            .withSpecs(specs)
            .withModuleTestReportsDir(moduleTestReportsDir.get().asFile.absolutePath)
            .withRootTestReportsDir(rootTestReportsDir.get().asFile.absolutePath)
            .withInclude(include.orNull)
            .withTargetName(targetName.orNull)
//               .withCommandLineTags(tags.orNull)
            .configure(this)

         super.exec()
      }
   }
}
