
package io.kotest.extensions.allure

import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension

class ProjectConfig : AbstractProjectConfig() {

   override val specExecutionOrder = SpecExecutionOrder.Annotated
   override fun extensions(): List<Extension> =
      listOf(AllureTestReporter(includeContainers = false))
}
