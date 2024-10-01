package io.kotest.core.annotation.enabledif

import io.kotest.core.annotation.EnabledCondition
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

object NotMacCondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = !System.getProperty("os.name").lowercase().contains("mac")
}

object MacCondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = System.getProperty("os.name").lowercase().contains("mac")
}

object LinuxCondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = System.getProperty("os.name").lowercase().contains("linux")
}

object NotLinuxCondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = !System.getProperty("os.name").lowercase().contains("linux")
}

object WindowsCondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = System.getProperty("os.name").lowercase().contains("windows")
}

object NotWindowsCondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = !System.getProperty("os.name").lowercase().contains("windows")
}
