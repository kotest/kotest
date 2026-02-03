package io.kotest.framework.gradle

import io.kotest.common.ExperimentalKotest

open class KotestGradleExtension {

   /**
    * Set to true and the Gradle plugin will create "kotest" test tasks
    */
   var customGradleTask = false

   /**
    * Set to true, and the Kotest engine will propagate ignore reasons to Gradle.
    */
   var showIgnoreReasons = false

   @ExperimentalKotest
   var alwaysRerunTests = false

   /**
    * Set to true, and the Gradle plugin will configure the Kotlin Power Assert plugin for use with the
    * Kotest assertion framework. Will automatically add the assertions library and power assert plugin,
    * if they are not already present.
    */
   @ExperimentalKotest
   var enablePowerAssert = false
}
