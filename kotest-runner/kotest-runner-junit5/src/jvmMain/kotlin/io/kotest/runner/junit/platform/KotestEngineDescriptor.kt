package io.kotest.runner.junit.platform

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.filter.TestFilter
import io.kotest.core.spec.Spec
import io.kotest.engine.test.names.FallbackDisplayNameFormatter
import io.kotest.core.log
import io.kotest.runner.junit.platform.gradle.GradleClassMethodRegexTestFilter
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import kotlin.reflect.KClass

class KotestEngineDescriptor(
   id: UniqueId,
   internal val configuration: ProjectConfiguration,
   val classes: List<KClass<out Spec>>,
   val scripts: List<KClass<*>>,
   val testFilters: List<TestFilter>,
   val error: Throwable?, // an error during discovery
) : EngineDescriptor(id, "Kotest") {
   // Only reports dynamic children (see ExtensionExceptionExtractor) if there are no test classes to run.
   override fun mayRegisterTests(): Boolean = classes.isEmpty()
}

internal fun createEmptyEngineDescriptor(id: UniqueId, configuration: ProjectConfiguration): KotestEngineDescriptor {
   return KotestEngineDescriptor(id, configuration, emptyList(), emptyList(), emptyList(), null)
}

internal fun createEngineDescriptor(
   uniqueId: UniqueId,
   configuration: ProjectConfiguration,
   specs: List<KClass<out Spec>>,
   gradleClassMethodTestFilter: GradleClassMethodRegexTestFilter?,
   error: Throwable?,
): KotestEngineDescriptor {

   val engine = KotestEngineDescriptor(
      uniqueId,
      configuration,
      specs,
      emptyList(),
      listOfNotNull(gradleClassMethodTestFilter),
      error
   )

   val formatter = FallbackDisplayNameFormatter.default(configuration)

   log { "Adding ${specs.size} children to the root descriptor ${KotestEngineDescriptor::class}@${engine.hashCode()}" }
   specs.forEach {
      engine.addChild(createSpecTestDescriptor(engine, it.toDescriptor(), formatter.format(it)))
   }
   return engine
}
