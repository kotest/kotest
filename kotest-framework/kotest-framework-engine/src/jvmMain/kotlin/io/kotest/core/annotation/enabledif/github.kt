package io.kotest.core.annotation.enabledif

import io.kotest.core.annotation.Condition
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

//The operating system of the runner executing the job. Possible values are Linux, Windows, or macOS.
private fun runnerOs() = System.getenv("RUNNER_OS").lowercase()

class LinuxRunnerOsCondition : Condition {
   override fun evaluate(kclass: KClass<out Spec>): Boolean = runnerOs() == "linux"
}

class MacRunnerOsCondition : Condition {
   override fun evaluate(kclass: KClass<out Spec>): Boolean = runnerOs() == "macos"
}

class WindowsRunnerOsCondition : Condition {
   override fun evaluate(kclass: KClass<out Spec>): Boolean = runnerOs() == "windows"
}

/**
 * Returns true if the tests are running in a GitHub Actions environment.
 */
class GithubActionsCondition : Condition {
   override fun evaluate(kclass: KClass<out Spec>): Boolean = System.getenv("GITHUB_ACTIONS") == "true"
}

// used by kotest to enable tests only on linux if running in github actions
class LinuxOnlyGithubCondition : Condition {
   override fun evaluate(kclass: KClass<out Spec>): Boolean =
      // either we're not in github actions, or we must be on linux
      !GithubActionsCondition().evaluate(kclass) || LinuxRunnerOsCondition().evaluate(kclass)
}
