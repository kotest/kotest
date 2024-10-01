package io.kotest.core.annotation.enabledif

import io.kotest.core.annotation.EnabledCondition
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

class NotMacCondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = !System.getProperty("os.name").lowercase().contains("mac")
}

class MacCondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = System.getProperty("os.name").lowercase().contains("mac")
}

class LinuxCondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = System.getProperty("os.name").lowercase().contains("linux")
}

class NotLinuxCondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = !System.getProperty("os.name").lowercase().contains("linux")
}

class WindowsCondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = System.getProperty("os.name").lowercase().contains("windows")
}

class NotWindowsCondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = !System.getProperty("os.name").lowercase().contains("windows")
}
