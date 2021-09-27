import de.fayard.refreshVersions.core.StabilityLevel

plugins {
   id("de.fayard.refreshVersions") version "0.21.0"
}

refreshVersions {
   this.rejectVersionIf {
      candidate.stabilityLevel != StabilityLevel.Stable
   }
}
