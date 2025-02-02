package io.kotest.framework.gradle.tasks.run

import io.kotest.framework.gradle.tasks.BaseKotestTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.options.Option

sealed class BaseRunKotestTask : BaseKotestTask() {
   @get:Option(option = "descriptor", description = "Filter to a single spec or test")
   @get:Input
   @get:Optional
   abstract val descriptor: Property<String>

   @get:Option(option = "tests", description = "Filter to a test path expression")
   @get:Input
   @get:Optional
   abstract val tests: Property<String>

   @get:Option(option = "candidates", description = "The candidates list to avoid scanning")
   @get:Input
   @get:Optional
   abstract val candidates: Property<String>

   @get:Option(option = "packages", description = "Specify the packages to scan for tests")
   @get:Input
   @get:Optional
   abstract val packages: Property<String>

   @get:Option(option = "tags", description = "Set tag expression to include or exclude tests")
   @get:Input
   @get:Optional
   abstract val tags: Property<String>
}
