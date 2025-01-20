package io.kotest.plugin.intellij

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import javax.swing.Icon

class KotestConfigurationType : ConfigurationType {

   private val factory = KotestConfigurationFactory(this)

   override fun getIcon(): Icon = Icons().Kotest16

   override fun getConfigurationTypeDescription(): String = "Run tests with Kotest"

   override fun getId(): String = Constants.FrameworkId

   override fun getDisplayName(): String = Constants.FrameworkName

   override fun getConfigurationFactories(): Array<ConfigurationFactory> = arrayOf(factory)
}
