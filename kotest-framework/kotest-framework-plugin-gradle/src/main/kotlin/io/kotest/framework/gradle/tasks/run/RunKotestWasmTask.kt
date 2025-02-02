package io.kotest.framework.gradle.tasks.run

import org.gradle.api.tasks.TaskAction

abstract class RunKotestWasmTask internal constructor() : BaseRunKotestTask() {

   @TaskAction
   internal fun action() {
      // TODO...
   }
}
