package io.kotest.engine

import io.kotest.common.KotestInternal
import io.kotest.core.TagExpression
import io.kotest.core.filter.SpecFilter
import io.kotest.core.filter.TestFilter
import io.kotest.core.spec.Spec
import io.kotest.engine.listener.CompositeTestEngineListener
import io.kotest.engine.listener.PinnedSpecTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.listener.ThreadSafeTestEngineListener
import kotlin.reflect.KClass
import kotlin.script.templates.standard.ScriptTemplateWithArgs

/**
 * A builder class for creating and executing tests via the [TestEngine].
 */
@KotestInternal
@Suppress("DEPRECATION")
@Deprecated("Prefer TestEngineLauncher. Deprecated since 5.0")
class KotestEngineLauncher(
   private val listeners: List<TestEngineListener>,
   private val specs: List<KClass<out Spec>>,
   private val testFilters: List<TestFilter>,
   private val specFilters: List<SpecFilter>,
   private val tags: TagExpression?,
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
         tags: TagExpression?
      ): KotestEngineLauncher {
         return KotestEngineLauncher(
            listeners = listeners,
            specs = specs,
            scripts = emptyList(),
            testFilters = emptyList(),
            specFilters = emptyList(),
            tags = tags,
            dumpConfig = false,
         )
      }
   }

   constructor() : this(emptyList(), emptyList(), emptyList(), emptyList(), null, false, emptyList())

   @Deprecated("This class is deprecated since 5.0")
   fun launch(): EngineResult {

      if (listeners.isEmpty())
         error("Cannot launch a KotestEngine without at least one TestEngineListener")

      val launcher = TestEngineLauncher(
         ThreadSafeTestEngineListener(
            PinnedSpecTestEngineListener(
               CompositeTestEngineListener(listeners)
            )
         )
      ).withExtensions(testFilters)
         .withExtensions(specFilters)
         .withTagExpression(tags)
         .withClasses(specs)

      return launcher.launch()
   }

   @Deprecated("This class is deprecated since 5.0")
   fun withFilter(filter: TestFilter) = withFilters(listOf(filter))

   @Deprecated("This class is deprecated since 5.0")
   fun withListener(listener: TestEngineListener) = KotestEngineLauncher(
      listeners = this.listeners + listener,
      specs = specs,
      testFilters = testFilters,
      specFilters = specFilters,
      tags = tags,
      dumpConfig = dumpConfig,
      scripts = scripts,
   )

   @Deprecated("This class is deprecated since 5.0")
   fun withDumpConfig(dump: Boolean) = KotestEngineLauncher(
      listeners = listeners,
      specs = specs,
      testFilters = testFilters,
      specFilters = specFilters,
      tags = tags,
      dumpConfig = dump,
      scripts = scripts,
   )

   @Deprecated("This class is deprecated since 5.0")
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

   @Deprecated("This class is deprecated since 5.0")
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

   @Deprecated("This class is deprecated since 5.0")
   fun withFilters(filters: List<TestFilter>): KotestEngineLauncher = withTestFilters(filters)

   @Deprecated("This class is deprecated since 5.0")
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

   @Deprecated("This class is deprecated since 5.0")
   fun withSpec(klass: KClass<out Spec>) = withSpecs(listOf(klass))

   @Deprecated("This class is deprecated since 5.0")
   fun withSpecs(vararg specs: KClass<out Spec>) = withSpecs(specs.toList())

   @Deprecated("This class is deprecated since 5.0")
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

   @Deprecated("This class is deprecated since 5.0")
   fun withTags(tags: TagExpression?): KotestEngineLauncher {
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
