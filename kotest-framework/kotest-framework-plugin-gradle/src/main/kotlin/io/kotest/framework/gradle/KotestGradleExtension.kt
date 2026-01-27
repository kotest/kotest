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
}
