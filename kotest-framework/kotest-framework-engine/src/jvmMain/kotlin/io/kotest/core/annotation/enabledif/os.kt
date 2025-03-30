package io.kotest.core.annotation.enabledif

import io.kotest.core.annotation.EnabledCondition
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

private fun osName() = System.getProperty("os.name").lowercase()

class MacCondition : EnabledCondition {
   override fun evaluate(kclass: KClass<out Spec>): Boolean = osName().contains("mac")
}

class NotMacCondition : EnabledCondition {
   override fun evaluate(kclass: KClass<out Spec>): Boolean = !MacCondition().evaluate(kclass)
}

class LinuxCondition : EnabledCondition {
   override fun evaluate(kclass: KClass<out Spec>): Boolean = osName().contains("linux")
}

class NotLinuxCondition : EnabledCondition {
   override fun evaluate(kclass: KClass<out Spec>): Boolean = !LinuxCondition().evaluate(kclass)
}

class WindowsCondition : EnabledCondition {
   override fun evaluate(kclass: KClass<out Spec>): Boolean = osName().contains("windows")
}

class NotWindowsCondition : EnabledCondition {
   override fun evaluate(kclass: KClass<out Spec>): Boolean = !WindowsCondition().evaluate(kclass)
}

// used by kotest to disable tests on macos when running on github actions to speed up the builds
class NotMacOnGithubCondition : EnabledCondition {
   override fun evaluate(kclass: KClass<out Spec>): Boolean =
      !(MacCondition().evaluate(kclass) && GithubActionCondition().evaluate(kclass))
}
