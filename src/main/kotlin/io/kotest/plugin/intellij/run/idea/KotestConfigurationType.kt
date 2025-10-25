package io.kotest.plugin.intellij.run.idea

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import io.kotest.plugin.intellij.Constants
import io.kotest.plugin.intellij.Icons
import javax.swing.Icon

@Deprecated("Starting with Kotest 6 the preferred method is to run via gradle")
class KotestConfigurationType : ConfigurationType {

   private val factory = KotestConfigurationFactory(this)

   override fun getIcon(): Icon = Icons.KOTEST_16

   override fun getConfigurationTypeDescription(): String = "Run tests with Kotest"

   override fun getId(): String = Constants.FRAMEWORK_ID

   override fun getDisplayName(): String = Constants.FRAMEWORK_NAME

   override fun getConfigurationFactories(): Array<ConfigurationFactory> = arrayOf(factory)
}
