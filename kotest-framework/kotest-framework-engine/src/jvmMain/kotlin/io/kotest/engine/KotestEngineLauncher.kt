package io.kotest.engine

import io.kotest.core.Tags
import io.kotest.core.filter.SpecFilter
import io.kotest.core.filter.TestFilter
import io.kotest.core.spec.Spec
import io.kotest.engine.filter.RequiresTagSpecFilter
import io.kotest.engine.listener.CompositeTestEngineListener
import io.kotest.engine.listener.PinnedSpecTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.listener.ThreadSafeTestEngineListener
import io.kotest.engine.tags.ConfigurationTagProvider
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KClass
import kotlin.script.templates.standard.ScriptTemplateWithArgs

/**
 * A builder class for creating and executing tests via the [TestEngine].
 */
@Deprecated("Prefer TestEngineLauncher. This class will remain for compatibility with existing clients but new code should use the TestEngineLauncher which is multiplatform.")
class KotestEngineLauncher(
   private val listeners: List<TestEngineListener>,
   private val specs: List<KClass<out Spec>>,
   private val testFilters: List<TestFilter>,
   private val specFilters: List<SpecFilter>,
   private val tags: Tags?,
   private val dumpConfig: Boolean,
   private val scripts: List<KClass<out ScriptTemplateWithArgs>>,
) {

   companion object {

      /**
       * Returns a [KotestEngineLauncher] with common filters and settings.
       */
      fun default(
         listeners: List<TestEngineListener>,
         specs: List<KClass<out Spec>>,
         tags: Tags?
      ): KotestEngineLauncher {
         return KotestEngineLauncher(
            listeners = listeners,
            specs = specs,
            scripts = emptyList(),
            testFilters = emptyList(),
            specFilters = listOf(RequiresTagSpecFilter(ConfigurationTagProvider())),
            tags = tags,
            dumpConfig = false,
         )
      }
   }

   constructor() : this(emptyList(), emptyList(), emptyList(), emptyList(), null, false, emptyList())

    fun launch(): EngineResult {

       if (listeners.isEmpty())
          error("Cannot launch a KotestEngine without at least one TestEngineListener")

       val launcher = TestEngineLauncher(
          ThreadSafeTestEngineListener(
             PinnedSpecTestEngineListener(
                CompositeTestEngineListener(listeners)
             )
          )
      ).withTestFilters(testFilters)
         .withSpecFilters(specFilters)
         .withExplicitTags(tags)
         .withClasses(specs)

      return runBlocking { launcher.async() }
   }

   fun withFilter(filter: TestFilter) = withFilters(listOf(filter))

   fun withListener(listener: TestEngineListener) = KotestEngineLauncher(
      listeners = this.listeners + listener,
      specs = specs,
      testFilters = testFilters,
      specFilters = specFilters,
      tags = tags,
      dumpConfig = dumpConfig,
      scripts = scripts,
   )

   fun withDumpConfig(dump: Boolean) = KotestEngineLauncher(
      listeners = listeners,
      specs = specs,
      testFilters = testFilters,
      specFilters = specFilters,
      tags = tags,
      dumpConfig = dump,
      scripts = scripts,
   )

   fun withSpecFilters(filters: List<SpecFilter>): KotestEngineLauncher {
      return KotestEngineLauncher(
         listeners = listeners,
         specs = specs,
         testFilters = testFilters,
         specFilters = specFilters + filters,
         tags = tags,
         dumpConfig = dumpConfig,
         scripts = scripts,
      )
   }

   fun withTestFilters(filters: List<TestFilter>): KotestEngineLauncher {
      return KotestEngineLauncher(
         listeners = listeners,
         specs = specs,
         testFilters = testFilters + filters,
         specFilters = specFilters,
         tags = tags,
         dumpConfig = dumpConfig,
         scripts = scripts,
      )
   }

   @Deprecated("use withTestFilters. This must remain for binary compatibility.")
   fun withFilters(filters: List<TestFilter>): KotestEngineLauncher = withTestFilters(filters)

   fun withScripts(scripts: List<KClass<out ScriptTemplateWithArgs>>): KotestEngineLauncher {
      return KotestEngineLauncher(
         listeners = listeners,
         specs = specs,
         testFilters = testFilters,
         specFilters = specFilters,
         tags = tags,
         dumpConfig = dumpConfig,
         scripts = scripts,
      )
   }

   fun withSpec(klass: KClass<out Spec>) = withSpecs(listOf(klass))

   fun withSpecs(vararg specs: KClass<out Spec>) = withSpecs(specs.toList())

   fun withSpecs(specs: List<KClass<out Spec>>): KotestEngineLauncher {
      return KotestEngineLauncher(
         listeners = listeners,
         specs = specs,
         testFilters = testFilters,
         specFilters = specFilters,
         tags = tags,
         dumpConfig = dumpConfig,
         scripts = scripts,
      )
   }

   fun withTags(tags: Tags?): KotestEngineLauncher {
      return KotestEngineLauncher(
         listeners = listeners,
         specs = specs,
         testFilters = testFilters,
         specFilters = specFilters,
         tags = tags,
         dumpConfig = dumpConfig,
         scripts = scripts,
      )
   }

}
