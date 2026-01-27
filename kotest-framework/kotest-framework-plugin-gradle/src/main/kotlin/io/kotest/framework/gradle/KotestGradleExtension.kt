package io.kotest.framework.gradle

open class KotestGradleExtension {

   /**
    * Set to true and the Gradle plugin will create "kotest" test tasks
    */
   var customGradleTask = false

   /**
    * Set to true and the Kotest engine will propagate ignore reasons to Gradle.
    */
   var showIgnoreReasons = false

   /**
    * Set to false, and the Gradle plugin will not automatically add the engine dependency
    */
   var autoAddEngineDependency = true
}
