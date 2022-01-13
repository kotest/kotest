package io.kotest.engine.spec.interceptor

import io.kotest.common.KotestInternal
import io.kotest.common.flatMap
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Ignored
import io.kotest.core.annotation.enabledif.wrapper
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.Materializer
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.test.status.isEnabled
import io.kotest.mpp.Logger
import io.kotest.mpp.annotation
import io.kotest.mpp.bestName
import io.kotest.mpp.hasAnnotation
import io.kotest.mpp.newInstanceNoArgConstructor

@OptIn(KotestInternal::class)
class ActiveRootTestSpecInterceptor(
   private val listener: TestEngineListener,
   private val configuration: ProjectConfiguration,
) : SpecInterceptor {
   private val extensions = SpecExtensions(configuration.registry)
   private val logger = Logger(ActiveRootTestSpecInterceptor::class)
   private val materializer = Materializer(configuration)

   override suspend fun intercept(
      spec: Spec,
      fn: suspend (Spec) -> Result<Map<TestCase, TestResult>>
   ): Result<Map<TestCase, TestResult>> {
      val isIgnored = spec::class.hasAnnotation<Ignored>()
      logger.log { Pair(spec::class.bestName(), "@Ignored == $isIgnored") }

      if (isIgnored) {
         return runCatching { listener.specIgnored(spec::class, "Disabled by @Ignored") }
            .flatMap { extensions.ignored(spec::class, "Disabled by @Ignored") }
            .map { emptyMap() }
      }

      val isEnabledIf = spec::class.annotation<EnabledIf>()?.wrapper?.newInstanceNoArgConstructor()?.enabled(spec::class) ?: true
      logger.log { Pair(spec::class.bestName(), "EnabledIf == $isEnabledIf") }

      if (!isEnabledIf) {
         return runCatching { listener.specIgnored(spec::class, "Disabled by @EnabledIf") }
            .flatMap { extensions.ignored(spec::class, "Disabled by @EnabledIf") }
            .map { emptyMap() }
      }

      val isEnabled = materializer.materialize(spec).any { it.isEnabled(configuration).isEnabled }
      logger.log { Pair(spec::class.bestName(), "Any test root enabled == $isEnabled") }

      if (!isEnabled) {
         return Result.success(emptyMap())
      }

      return fn(spec)
   }
}
