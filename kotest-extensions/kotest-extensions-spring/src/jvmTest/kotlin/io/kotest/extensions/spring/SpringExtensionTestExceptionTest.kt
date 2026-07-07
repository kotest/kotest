package io.kotest.extensions.spring

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.TestExecutionListener as SpringTestExecutionListener

private class TestMethodBoomException(message: String) : RuntimeException(message)

// guards the deliberately-failing test so it only runs inside the in-process launcher below,
// and is not picked up when the whole suite runs.
private var includeFailingTest = false

/**
 * Regression test for https://github.com/kotest/kotest/issues/6094 — an exception thrown by a test
 * must be forwarded to the Spring test context manager (and therefore to its TestExecutionListeners),
 * instead of SpringExtension always passing null.
 */
class SpringExtensionTestExceptionTest : FunSpec() {
   init {
      test("a thrown test exception is forwarded to the spring test context manager callbacks") {
         ExceptionCapturingTestExecutionListener.reset()
         includeFailingTest = true
         try {
            // run the (deliberately failing) spring spec in an isolated engine so its failure is
            // collected here rather than failing this spec
            TestEngineLauncher()
               .withListener(CollectingTestEngineListener())
               .withSpecRefs(SpecRef.Reference(ThrowingSpringSpec::class))
               .execute()
         } finally {
            includeFailingTest = false
         }

         // afterTestMethod must receive the exception thrown by the test (issue #6094)
         ExceptionCapturingTestExecutionListener.afterTestMethodException
            .shouldBeInstanceOf<TestMethodBoomException>()
         ExceptionCapturingTestExecutionListener.afterTestMethodException?.message shouldBe "boom"
         // afterTestExecution must likewise receive it
         ExceptionCapturingTestExecutionListener.afterTestExecutionException
            .shouldBeInstanceOf<TestMethodBoomException>()
      }
   }
}

@TestExecutionListeners(
   ExceptionCapturingTestExecutionListener::class,
   mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS,
)
@SpringBootTest(classes = [Components::class])
@ApplyExtension(SpringExtension::class)
private class ThrowingSpringSpec : FunSpec() {
   init {
      if (includeFailingTest) {
         test("a test that throws") {
            throw TestMethodBoomException("boom")
         }
      }
   }
}

class ExceptionCapturingTestExecutionListener : SpringTestExecutionListener {

   override fun afterTestMethod(testContext: TestContext) {
      afterTestMethodException = testContext.testException
   }

   override fun afterTestExecution(testContext: TestContext) {
      afterTestExecutionException = testContext.testException
   }

   companion object {
      var afterTestMethodException: Throwable? = null
      var afterTestExecutionException: Throwable? = null
      fun reset() {
         afterTestMethodException = null
         afterTestExecutionException = null
      }
   }
}
