package io.kotest.framework.gradle

import io.kotest.framework.gradle.tasks.AbstractKotestJvmTask
import io.kotest.framework.gradle.tasks.KotestAndroidTask
import io.kotest.framework.gradle.tasks.KotestJsTask
import io.kotest.framework.gradle.tasks.KotestJvmTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("unused")
abstract class KotestPlugin : Plugin<Project> {

   companion object {
      const val DESCRIPTION = "Runs tests using Kotest"
      const val EXTENSION_NAME = "runKotest"

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

      // allows users to configure the test engine
      val kotestExtension = project.extensions.create<KotestExtension>(EXTENSION_NAME)

      configureTaskConventions(project)

      // Configure Kotlin JVM projects
      handleKotlinJvm(project)

      // Configure Kotlin multiplatform projects
      handleKotlinMultiplatform(project)

      // Configure Kotlin Android projects
      handleKotlinAndroid(project)
   }

   private fun configureTaskConventions(project: Project) {
      project.tasks.withType<AbstractKotestJvmTask>().configureEach {
         group = JavaBasePlugin.VERIFICATION_GROUP
         description = DESCRIPTION
      }
   }

   private fun handleKotlinJvm(project: Project) {
      project.plugins.withId(KOTLIN_JVM_PLUGIN) {
         // gradle best practice is to only apply to this project, and users add the plugin to each subproject
         // see https://docs.gradle.org/current/userguide/isolated_projects.html
         project.tasks.register(JVM_ONLY_TASK_NAME, KotestJvmTask::class) {
            inputs.files(project.tasks.withType<KotlinCompile>().map { it.outputs.files })
         }
      }
   }

   @Suppress("LABEL_NAME_CLASH")
   private fun handleKotlinMultiplatform(project: Project) {
      project.plugins.withId(KOTLIN_MULTIPLATFORM_PLUGIN) {
         project.extensions.configure<KotlinMultiplatformExtension> {
            this.testableTargets.configureEach {
               println("testable target name ${this.name}")
               println("testable target targetName ${this.targetName}")
               println("testable disambiguationClassifier ${this.disambiguationClassifier}")
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
                        }
                     }

                     KotlinPlatformType.wasm -> println("Todo wasm")
                     KotlinPlatformType.common -> println("Todo common")
                     KotlinPlatformType.jvm -> println("Todo jvm")
                     KotlinPlatformType.androidJvm -> println("Todo androidJvm")
                     KotlinPlatformType.native -> println("Todo native")
                  }
//                  when (targetName) {
//                     else -> {
//                        val capitalTarget = name.replaceFirstChar { it.uppercase() }
//                        // gradle best practice is to only apply to this project, and users add the plugin to each subproject
//                        // see https://docs.gradle.org/current/userguide/isolated_projects.html
//                        project.tasks.register("testing_$name", AbstractKotestTask::class) {
//                           inputs.files(project.tasks.named("${name}TestClasses").map { it.outputs.files })
//                        }
//                     }
//                  }
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

            // todo better way to detect the test compilations, or find a way to get android variants
            // by default will be debugUnitTest and releaseUnitTest
            val testCompilations = target.compilations.matching { it.name.endsWith("UnitTest") }

            testCompilations.configureEach {
               val compilation: KotlinCompilation<*> = this
               val capitalTarget = name.replaceFirstChar { it.uppercase() }
               // gradle best practice is to only apply to this project, and users add the plugin to each subproject
               // see https://docs.gradle.org/current/userguide/isolated_projects.html
               project.tasks.register("kotest$capitalTarget", KotestAndroidTask::class) {
                  compilationNames.set(listOf(compilation.name))
                  inputs.files(project.tasks.withType<KotlinCompile>().map { it.outputs.files })
               }
            }

            // add one special task that runs all compilations
            // todo can we just make a task that runs the other tasks above
            project.tasks.register("kotest", KotestAndroidTask::class) {
               compilationNames.set(testCompilations.map { it.name })
               inputs.files(project.tasks.withType<KotlinCompile>().map { it.outputs.files })
            }
         }
      }
   }

//   private fun handleNodeJS(project: Project) {
//      project.rootProject.tasks.withType(NodeJsSetupTask::class).whenTaskAdded {
//         project.tasks.register("jsKotest", KotestJsTask::class.java) {
//            dependsOn(":kotlinNodeJsSetup")
//            dependsOn(":build")
//         }
//      }
//   }
}
