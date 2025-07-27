package io.kotest.framework.gradle

import io.kotest.framework.gradle.tasks.AbstractKotestTask
import io.kotest.framework.gradle.tasks.KotestAndroidTask
import io.kotest.framework.gradle.tasks.KotestJsTask
import io.kotest.framework.gradle.tasks.KotestJvmTask
import io.kotest.framework.gradle.tasks.KotestNativeTask
import io.kotest.framework.gradle.tasks.KotestWasmTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
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
import org.jetbrains.kotlin.gradle.plugin.KotlinTargetWithTests
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

@Suppress("unused")
abstract class KotestPlugin : Plugin<Project> {

   companion object {
      const val TASK_DESCRIPTION = "Runs tests using Kotest"

      const val KOTEST_TASK_NAME_WASM = "wasmJsKotest"
      const val TARGET_NAME_WASM_JS = "wasmJs"

      const val TASK_BUILD = "build"
      const val TASK_WASM_JS_TEST_CLASSES = "wasmJsTestClasses"

      private val unsupportedTargets = listOf(
         "metadata"
      )
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
            sourceSetName.set("test")
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
                  val testableTarget: KotlinTargetWithTests<*, *> = this
                  if (name !in unsupportedTargets) {
                     when (platformType) {
                        KotlinPlatformType.js -> handleJs(testableTarget)
                        KotlinPlatformType.wasm -> handleWasm(testableTarget)
                        KotlinPlatformType.common -> Unit
                        KotlinPlatformType.jvm -> handleJvm(testableTarget)
                        KotlinPlatformType.androidJvm -> Unit
                        // some example values
                        // Testable target: linuxX64, platformType: native, disambiguationClassifier: linuxX64
                        // Testable target: mingwX64, platformType: native, disambiguationClassifier: mingwX64
                        KotlinPlatformType.native -> handleNative(testableTarget)
                     }
                  }
               }
         }
      }
   }

   private fun handleWasm(testableTarget: KotlinTargetWithTests<*, *>) {
      // gradle best practice is to only apply to this project, and users add the plugin to each subproject
      // see https://docs.gradle.org/current/userguide/isolated_projects.html
      testableTarget.project.plugins.apply(NodeJsPlugin::class.java)
      testableTarget.project.extensions.configure(NodeJsEnvSpec::class.java) {
         val spec = this
         val task = testableTarget.project.tasks.register("wasmJsNodeKotest", KotestWasmTask::class) {
            nodeExecutable.set(spec.executable)
            dependsOn(":wasmJsNodeTest")
            inputs.files(
               project.tasks.named("wasmJsNodeTest")
                  .map { it.outputs.files }
            )
         }
         // the ksp plugin will create a configuration named kspWasmJsTest that contains
         // the symbol processors used by the test configuration. We want to wire in
         // the kotest symbol processor to this configuration so the user doesn't have to manually do it
         wireKsp(testableTarget.project, kspConfigurationName(testableTarget))

         // this means this kotest task will be run when the user runs "gradle check"
         testableTarget.project.tasks.named(JavaBasePlugin.CHECK_TASK_NAME).configure { dependsOn(task) }
      }
   }

   private fun handleJvm(testableTarget: KotlinTargetWithTests<*, *>) {
      // gradle best practice is to only apply to this project, and users add the plugin to each subproject
      // see https://docs.gradle.org/current/userguide/isolated_projects.html
      val task = testableTarget.project.tasks.register("jvmKotest", KotestJvmTask::class) {
         sourceSetName.set("jvmTest")
         inputs.files(project.tasks.named("jvmTest").map { it.outputs.files })
      }
      // this means this kotest task will be run when the user runs "gradle check"
      testableTarget.project.tasks.named(JavaBasePlugin.CHECK_TASK_NAME).configure { dependsOn(task) }
   }

   private fun handleJs(testableTarget: KotlinTargetWithTests<*, *>) {
      testableTarget.project.plugins.apply(NodeJsPlugin::class.java)
      testableTarget.project.extensions.configure(NodeJsEnvSpec::class.java) {
         val spec = this
         val task = testableTarget.project.tasks.register("jsNodeKotest", KotestJsTask::class) {
            nodeExecutable.set(spec.executable)
            dependsOn(":kotlinNodeJsSetup")
            inputs.files(
               project.tasks.named("compileTestDevelopmentExecutableKotlinJs")
                  .map { it.outputs.files }
            )
         }
         // the ksp plugin will create a configuration named kspJsTest that contains
         // the symbol processors used by the test configuration. We want to wire in
         // the kotest symbol processor to this configuration so the user doesn't have to manually
         wireKsp(testableTarget.project, kspConfigurationName(testableTarget))

         // this means this kotest task will be run when the user runs "gradle check"
         testableTarget.project.tasks.named(JavaBasePlugin.CHECK_TASK_NAME).configure { dependsOn(task) }
      }
   }

   private fun handleNative(testableTarget: KotlinTargetWithTests<*, *>) {
      val kotestTaskName = nativeKotestTaskName(testableTarget)
      // gradle best practice is to only apply to this project, and users add the plugin to each subproject
      // see https://docs.gradle.org/current/userguide/isolated_projects.html
      val task = testableTarget.project.tasks.register(kotestTaskName, KotestNativeTask::class) {
         target.set(testableTarget)
         // this is the task that runs the linker for the tests, so we depend on it to ensure
         // the tests are compiled before we run them
         val linkDebugTestTaskName = linkDebugNativeTestTaskName(testableTarget)
         inputs.files(project.tasks.named(linkDebugTestTaskName).map { it.outputs.files })
      }
      // the ksp plugin will create a configuration for each target that contains
      // the symbol processors used by the test configuration. We want to wire in
      // the kotest symbol processor to this configuration so the user doesn't have to manually
      // do it for every different native target (there could be many!)
      wireKsp(testableTarget.project, kspConfigurationName(testableTarget))

      // this means this kotest task will be run when the user runs "gradle check"
      testableTarget.project.tasks.named(JavaBasePlugin.CHECK_TASK_NAME).configure { dependsOn(task) }
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
    * Returns the name of the KSP task for a test compilation.
    * The KSP plugin uses the format ksp<TargetName>Test eg kspLinuxX64Test
    */
   private fun kspConfigurationName(compilation: KotlinTargetWithTests<*, *>): String {
      return "ksp${compilation.name.replaceFirstChar { it.uppercase() }}Test"
   }

   /**
    * Returns the name of the Kotest task for a native compilation.
    * This will mirror the standard test task naming convention by appending "Kotest" to the compilation name.
    * Eg `linuxX64Test` will become `linuxX64Kotest`, and `mingwX64Test` will become `mingwX64Kotest`.
    */
   private fun nativeKotestTaskName(compilation: KotlinTargetWithTests<*, *>): String {
      return compilation.name + "Kotest"
   }

   /**
    * Returns the name of the linker task for a native test compilation.
    * The format is linkDebugTest<TargetName>.
    * For example linkDebugTestLinuxX64.
    */
   private fun linkDebugNativeTestTaskName(compilation: KotlinTargetWithTests<*, *>): String {
      return "linkDebugTest${compilation.name.uppercaseFirstChar()}"
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
}
