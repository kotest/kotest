package io.kotest.framework.gradle

import io.kotest.common.ExperimentalKotest
import org.gradle.api.provider.Property

abstract class KotestGradleExtension {

   /**
    * Set to true and the Gradle plugin will create "kotest" test tasks
    */
   abstract val customGradleTask: Property<Boolean>

   /**
    * Set to true, and the Kotest engine will propagate ignore reasons to Gradle.
    */
   @ExperimentalKotest
   abstract val showIgnoreReasons: Property<Boolean>

   @ExperimentalKotest
   abstract val alwaysRerunTests: Property<Boolean>

   /**
    * Set to true, and the Gradle plugin will configure the Kotlin Power Assert plugin for use with the
    * Kotest assertion framework. Will automatically add the assertions library and power assert plugin,
    * if they are not already present.
    */
   @ExperimentalKotest
   abstract val enablePowerAssert: Property<Boolean>

   /**
    * Set to true, and Kotest will truncate container (parent) test display names when running via Gradle.
    * This can help avoid excessively long file paths in test reports when Gradle generates a directory
    * for every level of the test hierarchy (Gradle 9.3+).
    * Spec names (class names) are never truncated.
    */
   abstract val truncateTestNamesInGradle: Property<Boolean>
}
