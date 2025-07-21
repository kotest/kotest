package io.kotest.framework.gradle

import io.kotest.framework.gradle.tasks.AbstractKotestTask
import io.kotest.framework.gradle.tasks.KotestAndroidTask
import io.kotest.framework.gradle.tasks.KotestJsTask
import io.kotest.framework.gradle.tasks.KotestJvmTask
import io.kotest.framework.gradle.tasks.KotestNativeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinTargetWithTests
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("unused")
abstract class KotestPlugin : Plugin<Project> {

   companion object {
      const val DESCRIPTION = "Runs tests using Kotest"
      const val EXTENSION_NAME = "kotest"

      // this is used when the project is JVM only, i.e. not multiplatform, so instead of jvmKotest
      // we just use kotest to match the convention of test
      const val JVM_ONLY_TASK_NAME = "kotest"

      const val JS_TASK_NAME = "jsKotest"
      const val WASM_JS_TASK_NAME = "wasmJsKotest"

      const val TARGET_JS_NAME = "js"
      const val TARGET_NAME_WASM_JS = "wasmJs"

      const val TASK_NODE_JS_SETUP = "kotlinNodeJsSetup"
      const val TASK_BUILD = "build"
      const val TASK_COMPILE_TEST_DEV_JS = "compileTestDevelopmentExecutableKotlinJs"
      const val TASK_WASM_JS_TEST_CLASSES = "wasmJsTestClasses"

      private const val KOTLIN_JVM_PLUGIN = "org.jetbrains.kotlin.jvm"
      private const val KOTLIN_MULTIPLATFORM_PLUGIN = "org.jetbrains.kotlin.multiplatform"
      private const val KOTLIN_ANDROID_PLUGIN = "org.jetbrains.kotlin.android"

      private val unsupportedTargets = listOf(
         "metadata"
      )
   }

   override fun apply(project: Project) {

      configureTaskConventions(project)

      // Configure Kotlin JVM projects
      handleKotlinJvm(project)

      // Configure Kotlin multiplatform projects
      handleKotlinMultiplatform(project)

      // Configure Kotlin Android projects
      handleKotlinAndroid(project)
   }

   private fun configureTaskConventions(project: Project) {
      project.tasks.withType<AbstractKotestTask>().configureEach {
         group = JavaBasePlugin.VERIFICATION_GROUP
         description = DESCRIPTION
      }
   }

   private fun handleKotlinJvm(project: Project) {
      project.plugins.withId(KOTLIN_JVM_PLUGIN) {
         // gradle best practice is to only apply to this project, and users add the plugin to each subproject
         // see https://docs.gradle.org/current/userguide/isolated_projects.html
         val task = project.tasks.register(JVM_ONLY_TASK_NAME, KotestJvmTask::class) {
            inputs.files(project.tasks.withType<KotlinCompile>().map { it.outputs.files })
         }
         // this means this kotest task will be run when the user runs "gradle check"
         project.tasks.named(JavaBasePlugin.CHECK_TASK_NAME).configure { dependsOn(task) }
      }
   }

   @Suppress("LABEL_NAME_CLASH")
   private fun handleKotlinMultiplatform(project: Project) {
      project.plugins.withId(KOTLIN_MULTIPLATFORM_PLUGIN) { // this is the multiplatform plugin, not the kotlin plugin
         project.extensions.configure<KotlinMultiplatformExtension> { // this is the multiplatform extension
            val tasks = mutableSetOf<String>()
            this.testableTargets.configureEach { // are the targets that can run tests

               val testableTarget: KotlinTargetWithTests<*, *> = this

               if (name !in unsupportedTargets) {
                  when (platformType) {

                     KotlinPlatformType.js -> {
                        project.plugins.apply(NodeJsPlugin::class.java)
                        project.extensions.configure(NodeJsEnvSpec::class.java) {
                           project.tasks.register(JS_TASK_NAME, KotestJsTask::class) {
                              dependsOn(":$TASK_NODE_JS_SETUP")
                              dependsOn(":$TASK_COMPILE_TEST_DEV_JS")
                              nodeExecutable.set(this@configure.executable)
                              inputs.files(project.tasks.named(TASK_COMPILE_TEST_DEV_JS).map { it.outputs.files })
                           }
                           tasks.add(JS_TASK_NAME)
                        }
                     }

                     KotlinPlatformType.wasm -> println("Todo wasm")
                     KotlinPlatformType.common -> println("Todo common")
                     KotlinPlatformType.jvm -> println("Todo jvm")
                     KotlinPlatformType.androidJvm -> println("Todo androidJvm")

                     // testable name linuxX64
                     // testable targetName linuxX64
                     // testable disambiguationClassifier linuxX64
                     KotlinPlatformType.native -> {
                        // gradle best practice is to only apply to this project, and users add the plugin to each subproject
                        // see https://docs.gradle.org/current/userguide/isolated_projects.html
                        val kotestTaskName = testableTarget.name + "Kotest"
                        val linkDebugTestTaskName = "linkDebugTest${testableTarget.name.uppercaseFirstChar()}"
                        project.tasks.register(kotestTaskName, KotestNativeTask::class) {
                           target.set(testableTarget)
                           inputs.files(project.tasks.named(linkDebugTestTaskName).map { it.outputs.files })
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private fun handleKotlinAndroid(
      project: Project
   ) {
      project.plugins.withId(KOTLIN_ANDROID_PLUGIN) {
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
}
