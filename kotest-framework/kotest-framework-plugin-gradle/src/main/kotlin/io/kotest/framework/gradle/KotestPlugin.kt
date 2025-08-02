package io.kotest.framework.gradle

import io.kotest.framework.gradle.tasks.AbstractKotestTask
import io.kotest.framework.gradle.tasks.KotestAndroidTask
import io.kotest.framework.gradle.tasks.KotestJsTask
import io.kotest.framework.gradle.tasks.KotestJvmTask
import io.kotest.framework.gradle.tasks.KotestNativeTask
import io.kotest.framework.gradle.tasks.KotestWasmTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.StopExecutionException
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinTargetWithTests
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinTargetWithNodeJsDsl
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

@Suppress("unused")
abstract class KotestPlugin : Plugin<Project> {

   companion object {
      const val TASK_DESCRIPTION = "Runs tests using Kotest"
      const val TESTS_DIR_NAME = "test-results"
      private val unsupportedTargets = listOf("metadata")
   }

   private val version = version()

   override fun apply(project: Project) {

      configureTaskConventions(project)

      // Configure Kotlin JVM projects
      handleKotlinJvm(project)

      // Configure Kotlin multiplatform projects which covers native and js
      handleKotlinMultiplatform(project)

      // Configure Kotlin Android projects
      handleKotlinAndroid(project)
   }

   /**
    * Loads the version of this plugin from the kotest.gradle.properties file.
    * This version is used to set the version of the kotest framework symbol processor.
    */
   private fun version(): String {
      val props = Properties()
      props.load(this::class.java.getResourceAsStream("/kotest.gradle.properties"))
      return props.getProperty("version")
   }

   /**
    * As kotest tasks are added, this configures them with a group and description, and sets up
    * check to run them, so we don't have to do it in each task.
    */
   private fun configureTaskConventions(project: Project) {
      project.tasks.withType<AbstractKotestTask>().configureEach {
         group = JavaBasePlugin.VERIFICATION_GROUP
         description = TASK_DESCRIPTION
      }
   }

   private fun handleKotlinJvm(project: Project) {
      project.plugins.withType<KotlinPluginWrapper> {
         // gradle best practice is to only apply to this project, and users add the plugin to each subproject
         // see https://docs.gradle.org/current/userguide/isolated_projects.html
         // when we have a JVM project, the task name is just "kotest" to match the standard "test" task name.
         val task = project.tasks.register("kotest", KotestJvmTask::class) {

            val java = project.extensions.getByType(JavaPluginExtension::class.java)
            val sourceSet = java.sourceSets.findByName("test")
               ?: throw StopExecutionException("Could not find source set '${sourceSetClasspath.get()}'")

            sourceSetClasspath.set(sourceSet.runtimeClasspath)
            testReportsDir.set(getTestReportsDir(project, name))

            inputs.files(project.tasks.withType<KotlinCompile>().map { it.outputs.files })
         }
         // this means this kotest task will be run when the user runs "gradle check"
         project.tasks.named(JavaBasePlugin.CHECK_TASK_NAME).configure { dependsOn(task) }
      }
   }

   private fun handleKotlinMultiplatform(project: Project) {
      project.plugins.withType<KotlinMultiplatformPluginWrapper> { // this is the multiplatform plugin, not the kotlin plugin
         project.extensions.configure<KotlinMultiplatformExtension> { // this is the multiplatform extension
            // are the targets that can run tests
            this.testableTargets
               // we don't want to wire stuff to non-buildable targets (i.e. ios target on a linux host)
               // as this could make checkKotlinGradlePluginConfigurationErrors fail
               .matching { it.publishable }
               .configureEach {
                  val target = this
                  if (name !in unsupportedTargets) {
                     when (platformType) {
                        KotlinPlatformType.androidJvm -> Unit
                        KotlinPlatformType.common -> Unit
                        KotlinPlatformType.jvm -> handleMultiplatformJvm(target)
                        KotlinPlatformType.js -> handleJs(target)
                        // some example values
                        // Testable target: linuxX64, platformType: native, disambiguationClassifier: linuxX64
                        // Testable target: mingwX64, platformType: native, disambiguationClassifier: mingwX64
                        KotlinPlatformType.native -> handleNative(target)
                        KotlinPlatformType.wasm -> handleWasm(target)
                     }
                  }
               }
         }
      }
   }

   private fun handleMultiplatformJvm(target: KotlinTarget) {
      // gradle best practice is to only apply to this project, and users add the plugin to each subproject
      // see https://docs.gradle.org/current/userguide/isolated_projects.html
      val task = target.project.tasks.register("jvmKotest", KotestJvmTask::class) {

         val java = project.extensions.getByType(JavaPluginExtension::class.java)
         val sourceSet = java.sourceSets.findByName("jvmTest")
            ?: throw StopExecutionException("Could not find source set '${sourceSetClasspath.get()}'")

         sourceSetClasspath.set(sourceSet.runtimeClasspath)
         testReportsDir.set(getTestReportsDir(project, name))

         inputs.files(project.tasks.named("jvmTest").map { it.outputs.files })
      }
      // this means this kotest task will be run when the user runs "gradle check"
      target.project.tasks.named(JavaBasePlugin.CHECK_TASK_NAME).configure { dependsOn(task) }
   }

   private fun handleNative(target: KotlinTarget) {
      val kotestTaskName = nativeKotestTaskName(target)
      // gradle best practice is to only apply to this project, and users add the plugin to each subproject
      // see https://docs.gradle.org/current/userguide/isolated_projects.html
      val task = target.project.tasks.register(kotestTaskName, KotestNativeTask::class) {
         testReportsDir.set(getTestReportsDir(project, name))

         val kexe = project.layout.buildDirectory.get().asFile.resolve(nativeBinaryPath(target)).absolutePath
         exe.set(kexe)

         // this is the task that runs the linker for the tests, so we depend on it to ensure
         // the tests are compiled before we run them
         val linkDebugTestTaskName = linkDebugNativeTestTaskName(target)
         inputs.files(project.tasks.named(linkDebugTestTaskName).map { it.outputs.files })
      }
      // the ksp plugin will create a configuration for each target that contains
      // the symbol processors used by the test configuration. We want to wire in
      // the kotest symbol processor to this configuration so the user doesn't have to manually
      // do it for every different native target (there could be many!)
      wireKsp(target.project, kspConfigurationName(target))

      // this means this kotest task will be run when the user runs "gradle check"
      target.project.tasks.named(JavaBasePlugin.CHECK_TASK_NAME).configure { dependsOn(task) }
   }

   // wasmJs and wasmWasi land here, so we must not use hardcoded names
   private fun handleWasm(target: KotlinTargetWithTests<*, *>) {
      target.project.plugins.apply(NodeJsPlugin::class.java)
//      if (target is KotlinWasmTargetDsl) {
//          wasmTargetType will be either JS or WASI
//         println("KotlinWasmTargetDsl ${target.name} with wasmTargetType ${target.wasmTargetType}")
//      }
      // we only support node based wasm targets
      if (target is KotlinTargetWithNodeJsDsl) {
         // we need to use the NodeJsEnvSpec to ensure the node executable is available
         target.project.extensions.configure(NodeJsEnvSpec::class.java) {
            val nodeJsSpec = this
            // gradle best practice is to only apply to this project, and users add the plugin to each subproject
            // see https://docs.gradle.org/current/userguide/isolated_projects.html
            val task = target.project.tasks.register(wasmNodeKotestTaskName(target), KotestWasmTask::class) {
               nodeExecutable.set(nodeJsSpec.executable)
               // we could depend instead on artifactsTaskName ?
               dependsOn(wasmNodeTestTaskName(target))
               inputs.files(
                  project.tasks.named(wasmNodeTestTaskName(target))
                     .map { it.outputs.files }
               )
            }
            // the ksp plugin will create a configuration named kspWasmJsTest that contains
            // the symbol processors used by the test configuration. We want to wire in
            // the kotest symbol processor to this configuration so the user doesn't have to manually do it
            wireKsp(target.project, kspConfigurationName(target))

            // this means this kotest task will be run when the user runs "gradle check"
            target.project.tasks.named(JavaBasePlugin.CHECK_TASK_NAME).configure { dependsOn(task) }
         }
      }
   }

   private fun handleJs(target: KotlinTarget) {
      target.project.plugins.apply(NodeJsPlugin::class.java)
      target.project.extensions.configure(NodeJsEnvSpec::class.java) {
         val spec = this
         val task = target.project.tasks.register("jsNodeKotest", KotestJsTask::class) {
            nodeExecutable.set(spec.executable)

            val buildDir = project.layout.buildDirectory.asFile.get().toPath()
            val testModulePath = buildDir.resolve(testModulePath(project))
            moduleFile.set(testModulePath)

            dependsOn(":kotlinNodeJsSetup")
            inputs.files(
               project.tasks.named("compileTestDevelopmentExecutableKotlinJs")
                  .map { it.outputs.files }
            )
         }
         // the ksp plugin will create a configuration named kspJsTest that contains
         // the symbol processors used by the test configuration. We want to wire in
         // the kotest symbol processor to this configuration so the user doesn't have to manually
         wireKsp(target.project, kspConfigurationName(target))

         // this means this kotest task will be run when the user runs "gradle check"
         target.project.tasks.named(JavaBasePlugin.CHECK_TASK_NAME).configure { dependsOn(task) }
      }
   }

   private fun handleKotlinAndroid(project: Project) {
      project.plugins.withType<KotlinAndroidPluginWrapper> {
         project.extensions.configure<KotlinAndroidExtension> {

            // example compilations for a typical project:
            // [debug, debugAndroidTest, debugUnitTest, release, releaseUnitTest]

            // kotest only applies for unit tests, not instrumentation tests, so we can filter to
            // compilations that ends with UnitTest. In a standard android project these would be debugUnitTest
            // and releaseUnitTest, but if someone has custom build types then there could be more.
            val unitTestCompilations = target.compilations.matching { it.name.endsWith("UnitTest") }
            unitTestCompilations.configureEach {

               val compilation: KotlinCompilation<*> = this

               // gradle best practice is to only apply to this project, and users add the plugin to each subproject
               // see https://docs.gradle.org/current/userguide/isolated_projects.html
               val task = project.tasks.register(androidKotestTaskName(this), KotestAndroidTask::class) {
                  compilationName.set(compilation.name)
                  // we depend on the standard android test task to ensure compilation has happened
                  dependsOn(androidTestTaskName(compilation))
                  inputs.files(project.tasks.named(androidTestTaskName(compilation)).map { it.outputs.files })
               }

               // this means this kotest task will be run when the user runs "gradle check"
               project.tasks.named(JavaBasePlugin.CHECK_TASK_NAME).configure { dependsOn(task) }
            }
         }
      }
   }

   private fun androidTestTaskName(compilation: KotlinCompilation<*>): String {
      // this will result in something like testDebugUnitTest
      return "test" + compilation.name.replaceFirstChar { it.uppercase() }
   }

   private fun androidKotestTaskName(compilation: KotlinCompilation<*>): String {
      val capitalTarget = compilation.name.replaceFirstChar { it.uppercase() }
      // this will result in something like kotestDebugUnitTest, which is analogous to the
      // standard test task called testDebugUnitTest
      return "kotest$capitalTarget"
   }

   /**
    * The kotlin js compiler uses projectname-test as the module name, eg in build/js/packages
    */
   private fun testModulePath(project: Project): String {
      val testModuleName = "${project.name}-test"
      return "js/packages/${testModuleName}/kotlin/${testModuleName}.js"
   }

   /**
    * Returns the name of the KSP task for a test compilation.
    * The KSP plugin uses the format ksp<TargetName>Test eg kspLinuxX64Test
    */
   private fun kspConfigurationName(target: KotlinTarget): String {
      return "ksp${target.name.replaceFirstChar { it.uppercase() }}Test"
   }

   /**
    * Returns the name of the Kotest task for a native compilation.
    * This will mirror the standard test task naming convention by appending "Kotest" to the compilation name.
    * Eg `linuxX64Test` will become `linuxX64Kotest`, and `mingwX64Test` will become `mingwX64Kotest`.
    */
   private fun nativeKotestTaskName(target: KotlinTarget): String {
      return target.name + "Kotest"
   }

   private fun wasmNodeKotestTaskName(target: KotlinTarget): String {
      return "${target.name}NodeKotest"
   }

   private fun wasmNodeTestTaskName(target: KotlinTarget): String {
      return "${target.name}NodeTest"
   }

   /**
    * Returns the name of the linker task for a native test compilation.
    * The format is linkDebugTest<TargetName>.
    * For example linkDebugTestLinuxX64.
    */
   private fun linkDebugNativeTestTaskName(target: KotlinTarget): String {
      return "linkDebugTest${target.name.uppercaseFirstChar()}"
   }

   private fun nativeBinaryPath(target: KotlinTarget): String {
      // this will result in something like build/bin/linuxX64/debugTest/test.kexe
      return "bin/${target.name}/debugTest/test.kexe"
   }

   /**
    * 1. Checks for the presence of com.google.devtools.ksp and stops execution if missing
    * 2. Wires the Kotest symbol procesor for the target
    */
   internal fun wireKsp(project: Project, configurationName: String) {
      if (!project.pluginManager.hasPlugin("com.google.devtools.ksp")) {
         throw StopExecutionException(
            "KSP neither found in root project nor ${project.name}, " +
               "please add 'com.google.devtools.ksp' to the project's plugins"
         )
      }

      // when the ksp configuration we're looking for is created by the ksp plugin,
      // we will add the kotest symbol processor to it
      project.configurations.whenObjectAdded {
         if (name == configurationName) {
            // use the same version as this plugin
            project.dependencies.add(configurationName, "io.kotest:kotest-framework-symbol-processor:${version}")
         }
      }
   }

   private fun getTestReportsDir(project: Project, taskName: String): Provider<Directory> {
      val baseDirectory = project.layout.buildDirectory
      return baseDirectory.dir("$TESTS_DIR_NAME/$taskName")
   }
}
