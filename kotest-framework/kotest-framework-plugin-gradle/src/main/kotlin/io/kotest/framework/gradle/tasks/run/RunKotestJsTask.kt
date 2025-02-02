package io.kotest.framework.gradle.tasks.run

import org.gradle.api.tasks.TaskAction

abstract class RunKotestJsTask internal constructor() : BaseRunKotestTask() {

   @TaskAction
   internal fun action() {
      // TODO...
   }
}
