package io.kotest.runner.junit.platform

import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.extensions.Extension
import io.kotest.core.log
import io.kotest.core.spec.Spec
import io.kotest.engine.test.names.DisplayNameFormatting
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import kotlin.reflect.KClass

class KotestEngineDescriptor(
   id: UniqueId,
   val classes: List<KClass<out Spec>>,
   val extensions: List<Extension>,
) : EngineDescriptor(id, KotestJunitPlatformTestEngine.ENGINE_NAME) {
   // Only reports dynamic children (see ExtensionExceptionExtractor) if there are no test classes to run,
   // so that we can add a placeholder test for the error
   override fun mayRegisterTests(): Boolean = classes.isEmpty()
}

internal fun createEmptyEngineDescriptor(id: UniqueId): KotestEngineDescriptor {
   return KotestEngineDescriptor(id, emptyList(), emptyList())
}

internal fun createEngineDescriptor(
   uniqueId: UniqueId,
   specs: List<KClass<out Spec>>,
   extensions: List<Extension>, // extensions can be added via junit configuration parameters
): KotestEngineDescriptor {

   val engine = KotestEngineDescriptor(
      id = uniqueId,
      classes = specs,
      extensions = extensions,
   )

   val formatter = DisplayNameFormatting(null)

   log { "Adding ${specs.size} children to the root descriptor ${KotestEngineDescriptor::class}@${engine.hashCode()}" }
   specs.forEach {
      engine.addChild(createSpecTestDescriptor(engine, it.toDescriptor(), formatter.format(it)))
   }
   return engine
}
