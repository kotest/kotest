package io.kotest.core.config

import io.github.classgraph.ClassGraph
import io.kotest.fp.toOption

/**
 * Loads a config object from the underlying target.
 * For example, on the JVM it may scan the classpath.
 */
actual fun detectConfig(): ProjectConf {

   fun instantiate(klass: Class<*>): AbstractProjectConfig =
      when (val field = klass.declaredFields.find { it.name == "INSTANCE" }) {
         // if the static field for an object cannot be found, then instantiate
         null -> klass.newInstance() as AbstractProjectConfig
         // if the static field can be found then use it
         else -> field.get(null) as AbstractProjectConfig
      }

   fun from(fqn: String): ProjectConf {
      val conf = instantiate(Class.forName(fqn))
      return ProjectConf(
         extensions = conf.extensions(),
         testListeners = conf.listeners(),
         filters = conf.filters(),
         isolationMode = conf.isolationMode ?: conf.isolationMode(),
         assertionMode = conf.assertionMode,
         testCaseOrder = conf.testCaseOrder ?: conf.testCaseOrder(),
         specExecutionOrder = conf.specExecutionOrder ?: conf.specExecutionOrder(),
         failOnIgnoredTests = conf.failOnIgnoredTests,
         globalAssertSoftly = conf.globalAssertSoftly,
         testCaseConfig = conf.defaultTestCaseConfig
      )
   }

   val scanResult = ClassGraph()
      .enableClassInfo()
      .enableExternalClasses()
      .blacklistPackages("java.*", "javax.*", "sun.*", "com.sun.*", "kotlin.*")
      .scan()

   return scanResult
      .getSubclasses(AbstractProjectConfig::class.java.name)
      .map { it.name }
      .firstOrNull()
      .toOption()
      .fold({ ProjectConf() }, { from(it) })
}
