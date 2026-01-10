package io.kotest.runner.junit.platform

import io.kotest.core.extensions.Extension
import io.kotest.core.log
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.descriptor
import io.kotest.engine.test.names.DisplayNameFormatting
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.EngineDescriptor

class KotestEngineDescriptor internal constructor(
   id: UniqueId,
   val specs: List<SpecRef>,
   val extensions: List<Extension>,
) : EngineDescriptor(id, KotestJunitPlatformTestEngine.ENGINE_NAME) {
   // we add tests dynamically to the engine when there's an engine exception to be reported, and also
   // some plugins like the Gradle test-retry-plugin prune the root test descriptor, so they are re-registered
   override fun mayRegisterTests(): Boolean = true
}

internal data class EngineDescriptorBuilder(
   private val id: UniqueId,
   private val specs: List<SpecRef>,
   private val extensions: List<Extension>,
   private val formatter: DisplayNameFormatting,
) {

   companion object {
      fun builder(id: UniqueId): EngineDescriptorBuilder {
         return EngineDescriptorBuilder(id, emptyList(), emptyList(), DisplayNameFormatting(null))
      }
   }

   fun withSpecs(specs: List<SpecRef>): EngineDescriptorBuilder = copy(specs = specs)
   fun withExtensions(extensions: List<Extension>): EngineDescriptorBuilder = copy(extensions = extensions)
   fun withFormatter(formatting: DisplayNameFormatting): EngineDescriptorBuilder = copy(formatter = formatting)

   fun build(): KotestEngineDescriptor {
      val engine = KotestEngineDescriptor(id = id, specs = specs, extensions = extensions)
      log { "Adding ${specs.size} specs to the engine ${KotestEngineDescriptor::class}@${engine.hashCode()}" }
      specs.forEach {
         engine.addChild(createSpecTestDescriptor(engine, it.descriptor(), formatter.format(it.kclass)))
      }
      return engine
   }
}
