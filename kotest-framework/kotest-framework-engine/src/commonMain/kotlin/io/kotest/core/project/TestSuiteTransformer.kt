package io.kotest.core.project

import io.kotest.common.reflection.reflection
import io.kotest.core.Logger
import io.kotest.core.extensions.SpecExecutionOrderExtension
import io.kotest.core.spec.SpecRef
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.spec.DefaultSpecExecutionOrderExtension

/**
 * Simple adapter for filtering and sorting test suites prior to execution.
 */
internal fun interface TestSuiteTransformer {
   fun transform(suite: TestSuite): TestSuite
}

/**
 * For some reason, intellij is passing abstract classes to the Gradle runner when running a package.
 * This is a recent regression and could be caused by our plugin. The quickest fix is to filter out any
 * abstract classes at this stage.
 *
 * See https://github.com/kotest/kotest/issues/5635
 */
internal object FilterAbstractSpecsTransformer : TestSuiteTransformer {
   override fun transform(suite: TestSuite): TestSuite {
      return TestSuite(suite.specs.filter {
         when (it) {
            is SpecRef.Function -> true // these come from KSP and will be prefiltered
            is SpecRef.Reference -> !reflection.isAbstract(it.kclass)
         }
      })
   }
}

/**
 * Returns an updated [TestSuite] with specs sorted according to registered [SpecExecutionOrderExtension]s
 * or falling back to the [DefaultSpecExecutionOrderExtension].
 */
internal class SortSpecsTransformer(private val projectConfigResolver: ProjectConfigResolver) : TestSuiteTransformer {

   private val logger = Logger(this::class)

   override fun transform(suite: TestSuite): TestSuite {

      val exts = projectConfigResolver.extensions().filterIsInstance<SpecExecutionOrderExtension>().ifEmpty {
         listOf(DefaultSpecExecutionOrderExtension(projectConfigResolver))
      }

      logger.log { "Sorting specs using extensions $exts" }
      val specs = exts.fold(suite.specs) { acc, op -> op.sort(acc) }
      return TestSuite(specs)
   }
}
