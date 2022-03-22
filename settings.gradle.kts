import de.fayard.refreshVersions.core.StabilityLevel

plugins {
   // refreshVersions uses Kotlin 1.4.20 but Intellij 2022.1 requires Kotlin 1.6.0.
   // In order to fix the Kotlin version to 1.6.0, we need to set the Kotlin plugin
   // here already, because otherwise the Kotlin version from refreshVersions will
   // be used everywhere, leading to compiler errors.
   kotlin("jvm").version("1.6.0").apply(false)
   id("de.fayard.refreshVersions") version "0.21.0"
}

refreshVersions {
   this.rejectVersionIf {
      candidate.stabilityLevel != StabilityLevel.Stable
   }
}
