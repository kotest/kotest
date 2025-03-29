package io.kotest.core.annotation.enabledif

import io.kotest.core.annotation.EnabledCondition
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

private fun osName() = System.getProperty("os.name").lowercase()

class MacCondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = osName().contains("mac")
}

class NotMacCondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = !MacCondition().enabled(kclass)
}

class LinuxCondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = osName().contains("linux")
}

class NotLinuxCondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = !LinuxCondition().enabled(kclass)
}

class WindowsCondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = osName().contains("windows")
}

class NotWindowsCondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = !WindowsCondition().enabled(kclass)
}

class GithubActionCondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = System.getenv("GITHUB_ACTION") != null
}

// used by kotest to disable tests on macos when running on github actions to speed up the builds
internal class NotMacOnGithubCondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean =
      !NotMacCondition().enabled(kclass) || !GithubActionCondition().enabled(kclass)
}
