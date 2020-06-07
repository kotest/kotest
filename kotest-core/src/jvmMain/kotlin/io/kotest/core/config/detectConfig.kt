package io.kotest.core.config

import io.github.classgraph.ClassGraph
import io.kotest.core.extensions.Extension
import io.kotest.core.filters.Filter
import io.kotest.core.listeners.Listener
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.AutoScan
import io.kotest.fp.toOption
import kotlin.math.max

/**
 * Loads a config object from the underlying target by scanning the classpath
 * for instances of [AbstractProjectConfig].
 */
actual fun detectConfig(): ProjectConf {

   @Suppress("UNCHECKED_CAST")
   fun <T> instantiate(klass: Class<T>): T =
      when (val field = klass.declaredFields.find { it.name == "INSTANCE" }) {
         // if the static field for an object cannot be found, then instantiate
         null -> {
            val zeroArgsConstructor = klass.constructors.find { it.parameterCount == 0 }
                ?: throw IllegalArgumentException("Class ${klass.name} should have a zero-arg constructor")
            zeroArgsConstructor.newInstance() as T
         }
         // if the static field can be found then use it
         else -> field.get(null) as T
      }

   fun from(fqn: String): ProjectConf {
      val confClass = instantiate(Class.forName(fqn) as Class<AbstractProjectConfig>)
      val beforeAfterAllListener = object : ProjectListener {
         override suspend fun beforeProject() {
            confClass.beforeAll()
         }
         override suspend fun afterProject() {
            confClass.afterAll()
         }
      }
      return ProjectConf(
         extensions = confClass.extensions(),
         listeners = confClass.listeners() + confClass.projectListeners() + listOf(beforeAfterAllListener),
         filters = confClass.filters(),
         isolationMode = confClass.isolationMode ?: confClass.isolationMode(),
         assertionMode = confClass.assertionMode,
         testCaseOrder = confClass.testCaseOrder ?: confClass.testCaseOrder(),
         specExecutionOrder = confClass.specExecutionOrder ?: confClass.specExecutionOrder(),
         failOnIgnoredTests = confClass.failOnIgnoredTests,
         globalAssertSoftly = confClass.globalAssertSoftly,
         autoScanEnabled = confClass.autoScanEnabled ?: true,
         autoScanIgnoredClasses = confClass.autoScanIgnoredClasses,
         writeSpecFailureFile = confClass.writeSpecFailureFile ?: confClass.writeSpecFailureFile(),
         parallelism = max(confClass.parallelism, confClass.parallelism()),
         timeout = confClass.timeout,
         testCaseConfig = confClass.defaultTestCaseConfig,
         showDetailedDataClassDiff = confClass.showDetailedDataClassDiff
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
