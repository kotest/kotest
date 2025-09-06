package io.kotest.provided

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.DisplayNameFormatterExtension
import io.kotest.core.extensions.Extension
import io.kotest.core.test.TestCase
import io.kotest.engine.names.DisplayNameFormatter
import kotlin.reflect.KClass

class ProjectConfig : AbstractProjectConfig() {
   override val extensions: List<Extension> = listOf(
      object : DisplayNameFormatterExtension {
         override fun formatter(): DisplayNameFormatter {
            return object : DisplayNameFormatter {
               override fun format(kclass: KClass<*>): String {
                  return "wobble"
               }

               override fun format(testCase: TestCase): String {
                  return testCase.name.name
               }
            }
         }
      }
   )
}
