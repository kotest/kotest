package io.kotest.framework.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.options.Option

abstract class AbstractKotestTask internal constructor() : DefaultTask() {

   @get:Option(option = "descriptor", description = "Filter to a single spec or test")
   @get:Input
   @get:Optional
   abstract val descriptor: Property<String>

   @get:Option(option = "specs", description = "The specs list to avoid scanning")
   @get:Input
   @get:Optional
   abstract val specs: Property<String>

   @get:Option(option = "packages", description = "Specify the packages to limit after scanning")
   @get:Input
   @get:Optional
   abstract val packages: Property<String>

   @get:Option(option = "tags", description = "Set tag expression to include or exclude tests")
   @get:Input
   @get:Optional
   abstract val tags: Property<String>
}
