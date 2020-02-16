package io.kotest.core.config

import io.github.classgraph.ClassGraph
import io.kotest.core.extensions.Extension
import io.kotest.core.filters.Filter
import io.kotest.core.listeners.Listener
import io.kotest.core.spec.AutoScan
import io.kotest.fp.toOption

/**
 * Loads a config object from the underlying target by scanning the classpath
 * for instances of [AbstractProjectConfig].
 */
actual fun detectConfig(): ProjectConf {

   fun <T> instantiate(klass: Class<T>): T =
      when (val field = klass.declaredFields.find { it.name == "INSTANCE" }) {
         // if the static field for an object cannot be found, then instantiate
         null -> klass.constructors[0].newInstance() as T
         // if the static field can be found then use it
         else -> field.get(null) as T
      }

   fun from(fqn: String): ProjectConf {
      val conf = instantiate(Class.forName(fqn) as Class<AbstractProjectConfig>)
      return ProjectConf(
         extensions = conf.extensions(),
         listeners = conf.listeners() + conf.projectListeners(),
         filters = conf.filters(),
         isolationMode = conf.isolationMode ?: conf.isolationMode(),
         assertionMode = conf.assertionMode,
         testCaseOrder = conf.testCaseOrder ?: conf.testCaseOrder(),
         specExecutionOrder = conf.specExecutionOrder ?: conf.specExecutionOrder(),
         failOnIgnoredTests = conf.failOnIgnoredTests,
         globalAssertSoftly = conf.globalAssertSoftly,
         autoScanEnabled = conf.autoScanEnabled ?: true,
         autoScanIgnoredClasses = conf.autoScanIgnoredClasses,
         writeSpecFailureFile = conf.writeSpecFailureFile ?: conf.writeSpecFailureFile(),
         timeout = conf.timeout,
         testCaseConfig = conf.defaultTestCaseConfig
      )
   }

   val scanResult = ClassGraph()
      .enableClassInfo()
      .enableAnnotationInfo()
      .enableExternalClasses()
      .blacklistPackages("java.*", "javax.*", "sun.*", "com.sun.*", "kotlin.*", "kotlinx.*")
      .scan()

   val conf = scanResult
      .getSubclasses(AbstractProjectConfig::class.java.name)
      .map { it.name }
      .firstOrNull()
      .toOption()
      .fold({ ProjectConf() }, { from(it) })

   val autoscanned = if (conf.autoScanEnabled) {
      scanResult
         .getClassesWithAnnotation(AutoScan::class.java.name)
         .map { Class.forName(it.name) }
         .map { instantiate(it) }
   } else emptyList()

   return conf.copy(
      listeners = (conf.listeners + autoscanned.filterIsInstance<Listener>()).distinctBy { it::class.java.name },
      filters = conf.filters + autoscanned.filterIsInstance<Filter>().distinctBy { it::class.java.name },
      extensions = conf.extensions + autoscanned.filterIsInstance<Extension>().distinctBy { it::class.java.name }
   )
}
