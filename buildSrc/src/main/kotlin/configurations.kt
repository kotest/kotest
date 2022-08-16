

import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Bundling
import org.gradle.api.attributes.Bundling.BUNDLING_ATTRIBUTE
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.Category.CATEGORY_ATTRIBUTE
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE
import org.gradle.api.attributes.Usage.USAGE_ATTRIBUTE
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.named


fun Configuration.asProvider() {
   isVisible = false
   isCanBeResolved = false
   isCanBeConsumed = true
}


fun Configuration.asConsumer() {
   isVisible = false
   isCanBeResolved = true
   isCanBeConsumed = false
}


fun Configuration.mavenInternalAttributes(objects: ObjectFactory): Configuration =
   attributes {
      attribute(CATEGORY_ATTRIBUTE, objects.named("maven-internal"))
   }
