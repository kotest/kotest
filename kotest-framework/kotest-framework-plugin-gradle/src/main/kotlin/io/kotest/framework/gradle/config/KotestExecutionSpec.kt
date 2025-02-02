package io.kotest.framework.gradle.config

import io.kotest.framework.gradle.internal.utils.uppercaseFirstChar
import io.kotest.framework.gradle.tasks.run.BaseRunKotestTask
import io.kotest.framework.gradle.tasks.run.RunKotestJsTask
import io.kotest.framework.gradle.tasks.run.RunKotestJvmTask
import io.kotest.framework.gradle.tasks.run.RunKotestNativeTask
import io.kotest.framework.gradle.tasks.run.RunKotestWasmTask
import org.gradle.api.Named
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.toolchain.JavaLauncher
import org.gradle.kotlin.dsl.register
import javax.inject.Inject

/**
 * Base specification for launching Kotest tests.
 */
sealed class BaseKotestSpec(private val name: String) : Named {

//   abstract val enabled: Property<Boolean>

   override fun getName(): String = name

   abstract val runTestsTask: TaskProvider<out BaseRunKotestTask>
}

/**
 * Specification for launching Kotest Android JVM tests.
 */
abstract class KotestAndroidJvmSpec @Inject internal constructor(
   name: String,
   tasks: TaskContainer,
) : BaseKotestSpec(name) {
   abstract val classpath: ConfigurableFileCollection

   /** @see io.kotest.framework.gradle.KotestExtension.javaLauncher */
   abstract val javaLauncher: Property<JavaLauncher>

   override val runTestsTask: TaskProvider<RunKotestJvmTask> =
      tasks.register<RunKotestJvmTask>("kotest${name.uppercaseFirstChar()}") {
         description = "Runs Kotest Android JVM tests"
         this.runtimeClasspath.from(this@KotestAndroidJvmSpec.classpath)
         this.javaLauncher.convention(this@KotestAndroidJvmSpec.javaLauncher)
      }
}

/**
 * Specification for launching Kotest JVM tests.
 */
abstract class KotestJvmSpec @Inject internal constructor(
   name: String,
   tasks: TaskContainer,
) : BaseKotestSpec(name) {
   abstract val classpath: ConfigurableFileCollection

   /** @see io.kotest.framework.gradle.KotestExtension.javaLauncher */
   abstract val javaLauncher: Property<JavaLauncher>

   override val runTestsTask: TaskProvider<RunKotestJvmTask> =
      tasks.register<RunKotestJvmTask>("kotest${name.uppercaseFirstChar()}") {
         description = "Runs Kotest JVM tests"
         this.runtimeClasspath.from(this@KotestJvmSpec.classpath)
         this.javaLauncher.convention(this@KotestJvmSpec.javaLauncher)
      }
}

/**
 * Specification for launching Kotest JS tests.
 *
 * Not yet implemented.
 */
abstract class KotestJsSpec @Inject internal constructor(
   name: String,
   tasks: TaskContainer,
) : BaseKotestSpec(name) {

   override val runTestsTask: TaskProvider<RunKotestJsTask> =
      tasks.register<RunKotestJsTask>("kotest${name.uppercaseFirstChar()}") {
         description = "Runs Kotest JS tests"
      }
}

/**
 * Specification for launching Kotest Wasm tests.
 *
 * Not yet implemented.
 */
abstract class KotestWasmSpec @Inject internal constructor(
   name: String,
   tasks: TaskContainer,
) : BaseKotestSpec(name) {

   override val runTestsTask: TaskProvider<RunKotestWasmTask> =
      tasks.register<RunKotestWasmTask>("kotest${name.uppercaseFirstChar()}") {
         description = "Runs Kotest Wasm tests"
      }
}

/**
 * Specification for launching Kotest Native tests.
 *
 * Not yet implemented.
 */
abstract class KotestNativeSpec @Inject internal constructor(
   name: String,
   tasks: TaskContainer,
) : BaseKotestSpec(name) {
   override val runTestsTask: TaskProvider<RunKotestNativeTask> =
      tasks.register<RunKotestNativeTask>("kotest${name.uppercaseFirstChar()}") {
         description = "Runs Kotest Native tests"
      }
}
