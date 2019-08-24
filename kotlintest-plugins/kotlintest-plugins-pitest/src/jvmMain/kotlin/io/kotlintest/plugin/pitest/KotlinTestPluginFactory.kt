package io.kotlintest.plugin.pitest

import org.pitest.classinfo.ClassByteArraySource
import org.pitest.testapi.Configuration
import org.pitest.testapi.TestGroupConfig
import org.pitest.testapi.TestPluginFactory

class KotlinTestPluginFactory : TestPluginFactory {
  override fun createTestFrameworkConfiguration(config: TestGroupConfig?,
                                                source: ClassByteArraySource?,
                                                excludedRunners: MutableCollection<String>?,
                                                includedTestMethods: MutableCollection<String>?): Configuration {
    return KotlinTestConfiguration()
  }

  override fun description(): String = "KotlinTest Support"
  override fun name(): String = "KotlinTest"
}