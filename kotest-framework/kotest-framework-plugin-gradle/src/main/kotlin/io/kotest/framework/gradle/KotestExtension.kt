package io.kotest.framework.gradle

import org.gradle.api.provider.Property

abstract class KotestExtension internal constructor() {

   /**
    * Set a tag expression directly in the plugin configuration.
    * This can be useful if you want to run a subset of tests during development.
    */
   abstract val tagExpression: Property<String>

   /**
    * If true, then the build will fail if no spec classes are by the plugin.
    */
   abstract val failOnEmptySpecs: Property<Boolean>

   /**
    * The location of the compiled kotlin classes for Android builds.
    * By default, Android studio sets this to be build/tmp/kotlin-classes.
    * If a relative path, then assumes inside build, otherwise if non relative can be anywhere.
    */
   val androidTestSource: String = "tmp/kotlin-classes"
}
