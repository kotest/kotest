package io.kotest.runner.junit.platform

import io.kotest.core.extensions.Extension
import io.kotest.engine.extensions.DescriptorFilter
import io.kotest.core.log
import io.kotest.core.spec.Spec
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.engine.test.names.FallbackDisplayNameFormatter
import io.kotest.runner.junit.platform.gradle.GradleClassMethodRegexTestFilter
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import kotlin.reflect.KClass

class KotestEngineDescriptor(
   id: UniqueId,
   val classes: List<KClass<out Spec>>,
   val testFilters: List<DescriptorFilter>,
   val extensions: List<Extension>, // extensions can be added via junit configuration parameters
   val error: Throwable?, // an error during discovery
) : EngineDescriptor(id, KotestJunitPlatformTestEngine.ENGINE_NAME) {
   // Only reports dynamic children (see ExtensionExceptionExtractor) if there are no test classes to run.
   override fun mayRegisterTests(): Boolean = classes.isEmpty()
}

internal fun createEmptyEngineDescriptor(id: UniqueId): KotestEngineDescriptor {
   return KotestEngineDescriptor(id, emptyList(), emptyList(), emptyList(), null)
}

internal fun createEngineDescriptor(
   uniqueId: UniqueId,
   specs: List<KClass<out Spec>>,
   gradleClassMethodTestFilter: GradleClassMethodRegexTestFilter?,
   error: Throwable?,
   extensions: List<Extension>, // extensions can be added via junit configuration parameters
): KotestEngineDescriptor {

   val engine = KotestEngineDescriptor(
      id = uniqueId,
      classes = specs,
      testFilters = listOfNotNull(gradleClassMethodTestFilter),
      extensions = extensions,
      error = error,
   )

   val formatter = FallbackDisplayNameFormatter.default()

   log { "Adding ${specs.size} children to the root descriptor ${KotestEngineDescriptor::class}@${engine.hashCode()}" }
   specs.forEach {
      engine.addChild(createSpecTestDescriptor(engine, it.toDescriptor(), formatter.format(it)))
   }
   return engine
}
