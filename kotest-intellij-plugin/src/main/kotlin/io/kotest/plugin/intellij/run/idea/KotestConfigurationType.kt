package io.kotest.plugin.intellij.run.idea

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.ConfigurationTypeUtil
import io.kotest.plugin.intellij.Constants
import io.kotest.plugin.intellij.Icons
import javax.swing.Icon

// Producers must fetch the platform-registered singleton (rather than instantiating
// KotestConfigurationType themselves) so that run configurations created from different
// contexts are seen by IntelliJ as belonging to the same configuration type/factory. Without
// this, RunManager's lookup of existing configurations for a given factory never matches, and
// a new run configuration is created on every run instead of reusing the existing one.
fun getKotestConfigurationType(): KotestConfigurationType = ConfigurationTypeUtil.findConfigurationType(KotestConfigurationType::class.java)

@Deprecated("Starting with Kotest 6 the preferred method is to run via gradle")
class KotestConfigurationType : ConfigurationType {

   private val factory = KotestConfigurationFactory(this)

   override fun getIcon(): Icon = Icons.KOTEST_16

   override fun getConfigurationTypeDescription(): String = "Run tests with Kotest"

   override fun getId(): String = Constants.FRAMEWORK_ID

   override fun getDisplayName(): String = Constants.FRAMEWORK_NAME

   override fun getConfigurationFactories(): Array<ConfigurationFactory> = arrayOf(factory)

   fun getFactory(): ConfigurationFactory = factory
}
