package io.kotest.extensions.spring

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import kotlin.time.Duration.Companion.days

/**
 * Regression test for the "No TestContextManager defined in this coroutine context" failure
 * that occurred whenever `coroutineTestScope = true` was combined with [SpringExtension]:
 * `coroutineTestScope` runs the test body inside a fresh coroutine started by
 * `kotlinx.coroutines.test.runTest`, which previously dropped every ambient CoroutineContext
 * element except the TestCoroutineScheduler -- including the [SpringTestContextCoroutineContextElement]
 * that [SpringExtension] installs around spec execution.
 */
@ContextConfiguration(classes = [Components::class])
class SpringExtensionCoroutineTestScopeTest : FunSpec() {

   override val extensions = listOf(SpringExtension())

   @Autowired
   private lateinit var service: UserService

   init {
      test("test context should remain available in the coroutine context when coroutineTestScope is enabled")
         .config(coroutineTestScope = true) {
            // exercises virtual time, proving we're genuinely inside runTest's TestScope
            delay(2.days)
            testContextManager().shouldNotBeNull()
            service.repository.findUser().name shouldBe "system_user"
         }
   }
}
