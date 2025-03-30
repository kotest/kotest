package io.kotest.core.annotation.enabledif

import io.kotest.core.annotation.EnabledCondition
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

abstract class RunnerOsCondition : EnabledCondition {
   //The operating system of the runner executing the job. Possible values are Linux, Windows, or macOS.
   fun runneros(): String? = System.getenv("RUNNER_OS")?.lowercase()
}

class LinuxRunnerOsCondition : RunnerOsCondition() {
   override fun enabled(kclass: KClass<out Spec>): Boolean = runneros() == "linux"
}

class MacRunnerOsCondition : RunnerOsCondition() {
   override fun enabled(kclass: KClass<out Spec>): Boolean = runneros() == "macos"
}

class WindowsRunnerOsCondition : RunnerOsCondition() {
   override fun enabled(kclass: KClass<out Spec>): Boolean = runneros() == "windows"
}

/**
 * Returns true if the tests are running in a GitHub Actions environment.
 */
class GithubActionsCondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = System.getenv("GITHUB_ACTIONS") == "true"
}

// used by kotest to enable tests only on linux if running in github actions
class LinuxOnlyGithubCondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean =
      // either we're not in github actions, or we must be on linux
      !GithubActionsCondition().enabled(kclass) || LinuxRunnerOsCondition().enabled(kclass)
}
