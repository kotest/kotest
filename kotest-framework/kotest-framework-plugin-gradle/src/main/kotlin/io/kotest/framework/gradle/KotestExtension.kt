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
}
