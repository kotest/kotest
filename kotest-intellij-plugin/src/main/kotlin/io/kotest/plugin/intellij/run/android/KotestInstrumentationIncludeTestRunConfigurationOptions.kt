package io.kotest.plugin.intellij.run.android

import com.android.tools.idea.testartifacts.instrumented.TestRunConfigurationOptions
import com.intellij.execution.actions.ConfigurationContext

/**
 * [TestRunConfigurationOptions] adds extra options to a [com.android.tools.idea.testartifacts.instrumented.AndroidTestRunConfiguration].
 *
 * @see `https://github.com/JetBrains/android/blob/4b00e2c1896e90c096534c857d3b65f6e00694d4/project-system-gradle/src/com/android/tools/idea/run/configuration/AndroidBaselineProfileRunConfiguration.kt#L84`
 */
class KotestInstrumentationIncludeTestRunConfigurationOptions(
   private val filter: String
) : TestRunConfigurationOptions() {

   companion object {
      const val INSTRUMENTATION_INCLUDE_PATTERN_NAME = "INSTRUMENTATION_INCLUDE_PATTERN"
   }

   /**
    * Android docs:
    * You can pass custom parameters (e.g., -e server_url https://api.test.com) and retrieve them within your test
    * code using InstrumentationRegistry.getArguments().getString("server_url")
    */
   override fun getExtraOptions(context: ConfigurationContext): List<String> {
      // should match the name used by the InstrumentationFilter in the JUnit 4 runner
      return listOf("-e $INSTRUMENTATION_INCLUDE_PATTERN_NAME $filter")
   }
}
