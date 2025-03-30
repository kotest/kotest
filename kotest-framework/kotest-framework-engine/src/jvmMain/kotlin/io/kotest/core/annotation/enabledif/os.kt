package io.kotest.core.annotation.enabledif

import io.kotest.core.annotation.Condition
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

private fun osName() = System.getProperty("os.name").lowercase()

class MacCondition : Condition {
   override fun evaluate(kclass: KClass<out Spec>): Boolean = osName().contains("mac")
}

class NotMacCondition : Condition {
   override fun evaluate(kclass: KClass<out Spec>): Boolean = !MacCondition().evaluate(kclass)
}

class LinuxCondition : Condition {
   override fun evaluate(kclass: KClass<out Spec>): Boolean = osName().contains("linux")
}

class NotLinuxCondition : Condition {
   override fun evaluate(kclass: KClass<out Spec>): Boolean = !LinuxCondition().evaluate(kclass)
}

class WindowsCondition : Condition {
   override fun evaluate(kclass: KClass<out Spec>): Boolean = osName().contains("windows")
}

class NotWindowsCondition : Condition {
   override fun evaluate(kclass: KClass<out Spec>): Boolean = !WindowsCondition().evaluate(kclass)
}
