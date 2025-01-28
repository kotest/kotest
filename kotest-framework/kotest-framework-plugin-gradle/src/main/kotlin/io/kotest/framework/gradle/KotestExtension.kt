package io.kotest.framework.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class KotestExtension @Inject constructor(project: Project) {
   private val objects = project.objects

   /**
    * Set a tag expression directly in the plugin configuration.
    * This can be useful if you want to run a subset of tests during development.
    */
   val tagExpression: Property<String> = objects.property(String::class.java)

   /**
    * If true, then the build will fail if no spec classes are by the plugin.
    */
   val failOnEmptySpecs: Property<Boolean> = objects.property(Boolean::class.java)

   /**
    * The location of the compiled kotlin classes for Android builds.
    * By default, Android studio sets this to be build/tmp/kotlin-classes.
    * If a relative path, then assumes inside build, otherwise if non relative can be anywhere.
    */
   val androidTestSource: String = "tmp/kotlin-classes"
}
