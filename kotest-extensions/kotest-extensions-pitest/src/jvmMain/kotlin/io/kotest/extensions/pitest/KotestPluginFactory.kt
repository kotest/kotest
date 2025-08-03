package io.kotest.extensions.pitest

import org.pitest.classinfo.ClassByteArraySource
import org.pitest.testapi.Configuration
import org.pitest.testapi.TestGroupConfig
import org.pitest.testapi.TestPluginFactory

@Suppress("unused") // referenced in the service loader
class KotestPluginFactory : TestPluginFactory {
   override fun createTestFrameworkConfiguration(
      config: TestGroupConfig?,
      source: ClassByteArraySource?,
      excludedRunners: MutableCollection<String>?,
      includedTestMethods: MutableCollection<String>?
   ): Configuration {
      return KotestConfiguration()
   }

   override fun description(): String = "Kotest Support"
   override fun name(): String = "Kotest"
}
