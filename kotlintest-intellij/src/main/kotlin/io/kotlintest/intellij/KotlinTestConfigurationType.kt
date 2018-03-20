package io.kotlintest.intellij

import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.icons.AllIcons

class KotlinTestConfigurationType : ConfigurationTypeBase(
    "io.kotlintest.jvm",
    "KotlinTest",
    "Run test using KotlinTest",
    AllIcons.Debugger.MuteBreakpoints
) {
  init {
    addFactory(KotlinTestConfigurationFactory(this))
  }
}

