package io.kotest.framework.gradle

abstract class KotestExtension internal constructor() {

   /**
    * The location of the compiled kotlin classes for Android builds.
    * By default, Android studio sets this to be build/tmp/kotlin-classes.
    * If a relative path, then assumes inside build, otherwise if non relative can be anywhere.
    */
   val androidTestSource: String = "tmp/kotlin-classes"
}
