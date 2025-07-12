package io.kotest.framework.gradle

import io.kotest.framework.gradle.tasks.AbstractKotestTask
import io.kotest.framework.gradle.tasks.KotestAndroidTask
import io.kotest.framework.gradle.tasks.KotestJsTask
import io.kotest.framework.gradle.tasks.KotestJvmTask
import io.kotest.framework.gradle.tasks.KotestNativeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.testing.AbstractTestTask
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
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

      project.plugins.withType<KotlinMultiplatformPluginWrapper> {
         project.extensions.configure<KotlinMultiplatformExtension> {
            wireKotestKsp()
         }
      }

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
         project.tasks.register(JVM_ONLY_TASK_NAME, KotestJvmTask::class) {
            inputs.files(project.tasks.withType<KotlinCompile>().map { it.outputs.files })
         }
      }
   }

   @Suppress("LABEL_NAME_CLASH")
   private fun handleKotlinMultiplatform(project: Project) {
      project.plugins.withId(KOTLIN_MULTIPLATFORM_PLUGIN) { // this is the multiplatform plugin, not the kotlin plugin
         project.extensions.configure<KotlinMultiplatformExtension> { // this is the multiplatform extension
            val tasks = mutableSetOf<String>()
            this.testableTargets.configureEach { // are the targets that can run tests

               val testableTarget: KotlinTargetWithTests<*, *> = this

               println("testable name ${this.name}")
               println("testable targetName ${this.targetName}")
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
            // add one special task that runs all compilations
            // todo can we just make a task that runs the other tasks above?
//            project.tasks.register("kotest", AbstractKotestJvmTask::class) {
//               group = JavaBasePlugin.VERIFICATION_GROUP
//               description = DESCRIPTION
//               dependsOn(":$JS_TASK_NAME")
//            }
         }
      }
   }

   //TODO wire this to JVM test Sources automatically
   private fun KotlinDependencyHandler.addKotestJvmRunner() {
      project.logger.info("  Adding Kotest JUnit runner")
      implementation("io.kotest:kotest-runner-junit5-jvm")
   }

   /**
    * 1. Checks for the presence of KSP and stops execution if missing
    * 2. Wires the Kotest symbol procesor for every configured KMP target
    */
   internal fun KotlinMultiplatformExtension.wireKotestKsp() {
      if (!project.pluginManager.hasPlugin("com.google.devtools.ksp")) {
         throw StopExecutionException(
            "KSP neither found in root project nor ${project.name}, " +
               "please add 'com.google.devtools.ksp' to the either project's plugins"
         )
      }

      val version = System.getProperty("kotestVersion")
      project.configurations.whenObjectAdded {
         if (name.startsWith("ksp") && name.endsWith("Test")) {
            project.dependencies.add(name, "io.kotest:kotest-framework-symbol-processor-jvm:$version")
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
            // todo can we just make a task that runs the other tasks above?
//            project.tasks.register("kotest", AbstractKotestJvmTask::class) {
//               group = JavaBasePlugin.VERIFICATION_GROUP
//               description = DESCRIPTION
//            }
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
