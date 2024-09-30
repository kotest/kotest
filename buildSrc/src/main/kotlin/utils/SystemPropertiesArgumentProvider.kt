package utils

import org.gradle.api.Task
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.process.CommandLineArgumentProvider


/**
 * Provides Java system properties as a command line argument.
 *
 * This utility supports Gradle up-to-date checks.
 * - Keys will be registered as a [Task] inputs.
 * - The actual values will not be registered as task inputs, but their presence will be.
 *
 * If a key has a `null` value, then that system property will not be set.
 *
 * @see org.gradle.api.tasks.testing.Test.getJvmArgumentProviders
 */
class SystemPropertiesArgumentProvider(
   private val properties: Provider<Map<String, String?>>,
) : CommandLineArgumentProvider {

   // Don't register the actual values as a task inputs (so that Gradle caching is less sensitive),
   // but do register the presence of a value, in case someone add/removes one.
   @get:Input
   protected val inputs: Provider<Map<String, Boolean>>
      get() = properties.map { props -> props.mapValues { (_, v) -> v == null } }

   override fun asArguments(): Iterable<String> {
      val properties = properties.getOrElse(emptyMap())
      return properties
         .filterValues { v -> v != null }
         .map { (k, v) -> "-D$k=$v" }
   }

   companion object {
      fun SystemPropertiesArgumentProvider(keyValue: Provider<Pair<String, String?>>) =
         SystemPropertiesArgumentProvider(keyValue.map { mapOf(it) })
   }
}
