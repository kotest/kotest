package utils

import org.gradle.api.Task
import org.gradle.api.provider.Provider
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
   private val properties: Provider<Map<String, String>>,
) : CommandLineArgumentProvider {

   override fun asArguments(): Iterable<String> {
      val properties = properties.getOrElse(emptyMap())
      return properties
         .map { (k, v) -> "-D$k=$v" }
   }

   companion object {
      fun SystemPropertiesArgumentProvider(keyValue: Provider<Pair<String, String>>) =
         SystemPropertiesArgumentProvider(keyValue.map { mapOf(it) })
   }
}
