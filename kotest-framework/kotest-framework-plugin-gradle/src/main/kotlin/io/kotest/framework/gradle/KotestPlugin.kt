package io.kotest.framework.gradle

import io.kotest.framework.gradle.TestLauncherArgsJavaExecConfiguration.Companion.LAUNCHER_MAIN_CLASS
import io.kotest.framework.gradle.tasks.KotestAndroidTask
import io.kotest.framework.gradle.tasks.KotestAndroidTask.Companion.ARTIFACT_TYPE
import io.kotest.framework.gradle.tasks.KotestAndroidTask.Companion.TYPE_CLASSES_JAR
import io.kotest.framework.gradle.tasks.KotestJvmTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.Directory
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import org.jetbrains.kotlin.gradle.targets.js.KotlinWasmTargetType
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinNodeJsIr
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

@Suppress("unused")
abstract class KotestPlugin : Plugin<Project> {

   companion object {
      internal const val TASK_DESCRIPTION = "Runs kotest tests"
      internal const val TESTS_DIR_NAME = "test-results"
      internal const val JVM_KOTEST_NAME = "jvmKotest"
      internal const val JVM_TEST_NAME = "jvmTest"
      internal const val JSTEST_NAME = "jsTest"
      internal const val KSP_PLUGIN_ID = "com.google.devtools.ksp"
      private val unsupportedTargets = listOf("metadata")
   }

   private val version = System.getenv("KOTEST_DEV_KSP_VERSION") ?: version()

   override fun apply(project: Project) {

      project.tasks.register("kotest") {
         group = JavaBasePlugin.VERIFICATION_GROUP
         description = TASK_DESCRIPTION
      }

      // configures standalone Kotlin JVM projects
      handleKotlinJvm(project)

      // configures Kotlin multiplatform projects
      handleKotlinMultiplatform(project)

      // configure Kotlin Android projects when it is not a multiplatform project
      handleAndroid(project)
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

   private fun handleKotlinJvm(project: Project) {
      project.plugins.withType<KotlinPluginWrapper> {
         val existing = project.tasks.findByName("test")
         when (existing) {
            null -> println("> No test task found in project ${project.name} - no Kotest task will be added")
            is Test -> configureJvmTask("test", project, null) // no need for target name for standalone jvm
         }
      }
   }

   private fun configureJvmTask(sourceSetName: String, project: Project, target: String?) {
      // gradle best practice is to only apply to this project, and users add the plugin to each subproject
      // see https://docs.gradle.org/current/userguide/isolated_projects.html
      val jvmKotest = project.tasks.register(JVM_KOTEST_NAME, KotestJvmTask::class) {

         group = JavaBasePlugin.VERIFICATION_GROUP
         description = TASK_DESCRIPTION

         val java = project.extensions.getByType(JavaPluginExtension::class.java)
         val sourceSet = java.sourceSets.findByName(sourceSetName)
            ?: throw StopExecutionException("Could not find source set '$sourceSetName'")

         // I don't know why this has to be set here and not inside the exec method
         // it works for JVM but not KMP JVM
         // I think the KMP version must be shadowing the mainClass variable somewhere
         mainClass.set(LAUNCHER_MAIN_CLASS)
         classpath = sourceSet.runtimeClasspath

         // we don't want to abort test runs when we have test failures for one target
         isIgnoreExitValue = true

         // we need this to scan for specs at runtime
         testSourceSetClasspath.set(sourceSet.runtimeClasspath)

         moduleName.set(project.name)
         moduleTestReportsDir.set(getModuleTestReportsDir(project, name))
         rootTestReportsDir.set(getRootTestReportsDir(project, name))
         targetName.set(target)

         // we can execute check or test tasks with -Pkotest.include and this will then be
         // passed to the kotest runtime as an environment variable to filter specs and tests
         project.findProperty("kotest.include")?.let { include.set(it.toString()) }

         // these are the JVM compile tasks that produce the classes we want to test
         inputs.files(project.tasks.withType<KotlinCompile>().map { it.outputs.files })
      }

      project.tasks.getByName("kotest") {
         println("> Configuring kotest task for $JVM_KOTEST_NAME")
         dependsOn(jvmKotest)
      }
   }

   private fun handleKotlinMultiplatform(project: Project) {
      project.plugins.withType<KotlinMultiplatformPluginWrapper> { // this is the multiplatform plugin, not the kotlin plugin
         project.extensions.configure<KotlinMultiplatformExtension> { // this is the multiplatform extension
            this.targets
               .configureEach {
                  val target = this
                  if (name !in unsupportedTargets) {
                     when (platformType) {
                        KotlinPlatformType.androidJvm -> handleMultiplatformAndroid(target)
                        KotlinPlatformType.common -> Unit // these are not buildable targets, so we skip them
                        KotlinPlatformType.jvm -> handleMultiplatformJvm(target)
                        KotlinPlatformType.js -> handleJs(target)
                        // some example values
                        // Testable target: linuxX64, platformType: native, disambiguationClassifier: linuxX64
                        // Testable target: mingwX64, platformType: native, disambiguationClassifier: mingwX64
                        KotlinPlatformType.wasm -> handleWasm(target)
                        KotlinPlatformType.native -> {
                           // we don't want to wire stuff to non-buildable targets (i.e. ios target on a linux host)
                           // so we check if the target is publishable
                           if (target.publishable) handleNative(target)
                        }
                     }
                  }
               }
         }
      }
   }

   private fun handleMultiplatformJvm(target: KotlinTarget) {
      val existing = target.project.tasks.findByName(JVM_TEST_NAME)
      when (existing) {
         null -> println("> No $JVM_TEST_NAME task found in project ${target.project.name} - no $JVM_KOTEST_NAME task will be added")
         is KotlinJvmTest -> configureJvmTask(JVM_TEST_NAME, target.project, "jvm")
      }
   }

   private fun handleNative(target: KotlinTarget) {
      val nativeTaskName = nativeTestTaskName(target)
      when (val existing = target.project.tasks.findByName(nativeTaskName)) {

         // sometimes a native target might not exist, because either tests are not supported (eg android native)
         // or the target is not buildable on the current host (eg ios target on a linux host)
         null -> println("> Skipping tests for ${target.name} because no task $nativeTaskName found")

         is KotlinNativeTest -> {

            val moduleTestDir = getModuleTestReportsDir(target.project, existing.name).get()
            moduleTestDir.asFile.mkdirs()
            val moduleTestDirAbsolutePath = moduleTestDir.asFile.absolutePath

            val rootTestDir = getRootTestReportsDir(target.project, existing.name).get()
            rootTestDir.asFile.mkdirs()
            val rootTestDirAbsolutePath = rootTestDir.asFile.absolutePath

            // passed to the xml report generator
            val targetName = target.name

            // we can execute check or test tasks with -Pkotest.include and this will then be
            // passed to the kotest runtime as an environment variable to filter specs and tests
            val include = target.project.findProperty("kotest.include")

            existing.doFirst {

               if (include != null)
                  existing.environment("KOTEST_FRAMEWORK_RUNTIME_NATIVE_INCLUDE", include.toString())

               // we need to switch to TCSM format if running inside of intellij
               val listener = if (IntellijUtils.isIntellij()) "teamcity" else "console"
               existing.environment("KOTEST_FRAMEWORK_RUNTIME_NATIVE_LISTENER", listener)

               // it seems the kotlin native test task empties this directory, so this currently does not do anything
               existing.environment(
                  "KOTEST_FRAMEWORK_RUNTIME_NATIVE_MODULE_TEST_REPORTS_DIR",
                  moduleTestDirAbsolutePath
               )

               existing.environment(
                  "KOTEST_FRAMEWORK_RUNTIME_NATIVE_ROOT_TEST_REPORTS_DIR",
                  rootTestDirAbsolutePath
               )

               // this sets the target name in the environment, which is used by the xml report generator
               // to add the target name to the test names
               existing.environment("KOTEST_FRAMEWORK_RUNTIME_NATIVE_TARGET", targetName)
            }

            // the ksp plugin will create a configuration for each target that contains
            // the symbol processors used by the test configuration. We want to wire in
            // the kotest symbol processor to this configuration so the user doesn't have to manually
            // do it for every different native target (there could be many!)
            wireKsp(target.project, kspConfigurationName(target))

            target.project.tasks.getByName("kotest") {
               println("> Configuring kotest task for $nativeTaskName")
               dependsOn(existing)
            }
         }
      }
   }

   // wasmJs and wasmWasi land here, so we must not use hardcoded names
   private fun handleWasm(target: KotlinTarget) {
      if (target is KotlinJsIrTarget) {
         when (target.wasmTargetType) {
            KotlinWasmTargetType.JS -> {
               // the ksp plugin will create a configuration named kspWasmJsTest that contains
               // the symbol processors used by the test configuration. We want to wire in
               // the kotest symbol processor to this configuration so the user doesn't have to manually
               wireKsp(target.project, "kspWasmJsTest")
               target.project.tasks.getByName("kotest") {
                  println("> Configuring kotest task for wasmJsTest")
                  dependsOn(target.project.tasks["wasmJsTest"])
               }
            }

            KotlinWasmTargetType.WASI -> {
               target.subTargets.configureEach {
                  val subtarget = this
                  if (subtarget is KotlinNodeJsIr) { // we only support node based wasm targets
                     target.compilations.matching { it.name == KotlinCompilation.TEST_COMPILATION_NAME }.configureEach {
                        // todo this is disabled until we can find a way to disambiguate the wasi and non-wasi
                        // inside the KSP symbol processor because we need to generate different code
//
//                        // the ksp plugin will create a configuration named kspJsTest that contains
//                        // the symbol processors used by the test configuration. We want to wire in
//                        // the kotest symbol processor to this configuration so the user doesn't have to manually
//                        wireKsp(target.project, "kspWasmWasiTest")
                     }
                  }
               }
            }

            else -> Unit
         }
      }
   }

   private fun handleJs(target: KotlinTarget) {
      if (target is KotlinJsIrTarget) {
         // the ksp plugin will create a configuration named kspJsTest that contains
         // the symbol processors used by the test configuration. We want to wire in
         // the kotest symbol processor to this configuration so the user doesn't have to manually
         wireKsp(target.project, "kspJsTest")
         target.project.tasks.getByName("kotest") {
            println("> Configuring kotest task for $JSTEST_NAME")
            dependsOn(target.project.tasks[JSTEST_NAME])
         }
      }
   }

   private fun handleMultiplatformAndroid(target: KotlinTarget) {
      if (target is KotlinAndroidTarget) {
         configureAndroid(target.project, target.compilations, "android")
      }
   }

   private fun handleAndroid(project: Project) {
      project.plugins.withType<KotlinAndroidPluginWrapper> {
         project.extensions.configure<KotlinAndroidExtension> {
            configureAndroid(project, target.compilations, null)
         }
      }
   }

   private fun configureAndroid(
      project: Project,
      compilations: NamedDomainObjectContainer<out KotlinCompilation<out Any>>,
      target: String?,
   ) {
      // example compilations for a typical project:
      // [debug, debugAndroidTest, debugUnitTest, release, releaseUnitTest]

      // unitTest compilations are the ones that run on the JVM, not on an android device.

      // The androidTest compilations are the ones that run on an android device or simulator, also known as instrumentation tests.
      // debug and release are called build types in android speak

      // Kotest only supports unit tests, not instrumentation tests, so we can filter to
      // compilations that ends with UnitTest. In a standard android project these would be debugUnitTest
      // and releaseUnitTest, but if someone has custom build types then there could be more.
      compilations.matching { it.name.endsWith("UnitTest") }.configureEach {
         val compilation = this

         val runtimeDependencyConfigurationName = compilation.runtimeDependencyConfigurationName
            ?: error("No runtime dependency configuration found for compilation ${compilation.name}")

         val rt: Configuration = project.configurations.findByName(runtimeDependencyConfigurationName)
            ?: error("No configuration found for $runtimeDependencyConfigurationName")

         // filters the runtime files to only jars
         val runtimeFiles = rt.incoming.artifactView {
            attributes {
               attribute(ARTIFACT_TYPE, TYPE_CLASSES_JAR)
            }
         }.files

         // to run specs we need to include dependencies and the compiled output
         val runtimeWithTests = project.objects.fileCollection()
            .from(runtimeFiles)
            .from(compilation.output.allOutputs) // this is the compiled output from this compilation

         // gradle best practice is to only apply to this project, and users add the plugin to each subproject
         // see https://docs.gradle.org/current/userguide/isolated_projects.html
         val kotestTaskName = androidKotestTaskName(compilation)
         val task = project.tasks.register(kotestTaskName, KotestAndroidTask::class) {

            group = JavaBasePlugin.VERIFICATION_GROUP
            description = TASK_DESCRIPTION

            // I don't know why this has to be set here and not inside the exec method
            // it works for JVM but not KMP JVM
            // I think the KMP version must be shadowing the mainClass variable somewhere
            mainClass.set(LAUNCHER_MAIN_CLASS)
            classpath = runtimeWithTests

            // we don't want to abort test runs when we have test failures for one target
            isIgnoreExitValue = true

            // for specs we only care about what's outputted by this compilation
            specsClasspath.set(compilation.output.allOutputs)

            // we set the test reports dir to the standard android test reports dir
            // this will result in something like build/test-results/kotestDebugUnitTest
            moduleTestReportsDir.set(getModuleTestReportsDir(project, name))
            rootTestReportsDir.set(getRootTestReportsDir(project, name))
            compilationName.set(compilation.name)

            if (target != null)
               targetName.set(target + " " + androidBuildType(compilation))

            // we can execute check or test tasks with -Pkotest.include and this will then be
            // passed to the kotest runtime as an environment variable to filter specs and tests
            project.findProperty("kotest.include")?.let { include.set(it.toString()) }

            // we depend on the standard android test task to ensure compilation has happened
            dependsOn(androidTestTaskName(compilation))
            inputs.files(project.tasks.named(androidTestTaskName(compilation)).map { it.outputs.files })
         }

         // this means this kotest task will be run when the user runs "gradle kotest"
         project.tasks.getByName("kotest") {
            println("> Configuring kotest task for $kotestTaskName")
            dependsOn(task)
         }
      }
   }

   /**
    * Returns "release" or "debug" depending on the current build type, etc.
    */
   private fun androidBuildType(compilation: KotlinCompilation<*>): String {
      require(compilation.name.endsWith("UnitTest")) { "Only unit tests are supported" }
      return compilation.name.removeSuffix("UnitTest").lowercase()
   }

   /**
    * Returns the standard Android test task for a compilation.
    * For a compilation named "debugUnitTest", this will return "testDebugUnitTest".
    */
   private fun androidTestTaskName(compilation: KotlinCompilation<*>): String {
      // this will result in something like testDebugUnitTest
      return "test" + compilation.name.replaceFirstChar { it.uppercase() }
   }

   /**
    * Returns a name for a Kotest task aimed at an Android compilation.
    * For a compilation named "debugUnitTest", this will return "kotestDebugUnitTest", which
    * is an analogous name to the standard Android test task "testDebugUnitTest".
    */
   private fun androidKotestTaskName(compilation: KotlinCompilation<*>): String {
      val capitalTarget = compilation.name.replaceFirstChar { it.uppercase() }
      return "kotest$capitalTarget"
   }

   /**
    * Returns the name of the KSP task for a test compilation.
    * The KSP plugin uses the format ksp<TargetName>Test eg kspLinuxX64Test or kspWasmJsTest.
    */
   private fun kspConfigurationName(target: KotlinTarget): String {
      return "ksp${target.name.replaceFirstChar { it.uppercase() }}Test"
   }

   private fun nativeTestTaskName(target: KotlinTarget): String {
      return target.name + "Test"
   }

   /**
    * 1. Checks for the presence of com.google.devtools.ksp and stops execution if missing
    * 2. Wires the Kotest symbol procesor for the target
    */
   internal fun wireKsp(project: Project, configurationName: String) {
      if (!project.pluginManager.hasPlugin(KSP_PLUGIN_ID)) {
         throw StopExecutionException(
            "KSP neither found in root project nor ${project.name}, " +
               "please add 'com.google.devtools.ksp' to the project's plugins"
         )
      }

      // handles the case when the configuration is already created
      project.configurations.configureEach {
         if (name == configurationName) {
            project.dependencies.add(configurationName, "io.kotest:kotest-framework-symbol-processor:${version}")
         }
      }

      // handles the case when the configuration is created after this plugin is applied
      project.configurations.whenObjectAdded {
         if (name == configurationName) {
            // use the same version as this plugin
            project.dependencies.add(configurationName, "io.kotest:kotest-framework-symbol-processor:${version}")
         }
      }
   }

   private fun getModuleTestReportsDir(project: Project, taskName: String): Provider<Directory> {
      val baseDirectory = project.layout.buildDirectory
      return baseDirectory.dir("$TESTS_DIR_NAME/$taskName")
   }

   private fun getRootTestReportsDir(project: Project, taskName: String): Provider<Directory> {
      val baseDirectory = project.rootProject.layout.buildDirectory
      return baseDirectory.dir("$TESTS_DIR_NAME/$taskName")
   }
}
