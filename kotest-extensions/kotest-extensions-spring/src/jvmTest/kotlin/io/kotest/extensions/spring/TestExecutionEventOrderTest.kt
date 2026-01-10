package io.kotest.extensions.spring

import io.kotest.core.annotation.Description
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.event.annotation.AfterTestExecution
import org.springframework.test.context.event.annotation.AfterTestMethod
import org.springframework.test.context.event.annotation.BeforeTestExecution
import org.springframework.test.context.event.annotation.BeforeTestMethod

@Description("tests that the order of spring events is documented and defined")
@ContextConfiguration(classes = [Components::class])
@ApplyExtension(SpringExtension::class)
class TestExecutionEventOrderTest : FunSpec() {

   @Autowired
   private lateinit var service: LifecycleTestService

   init {
      test("definition of order of spring test execution events") {
         // need a test to trigger the events
      }
      afterSpec {
         events shouldBe listOf("beforeTestMethod", "beforeTestExecution", "afterTestExecution", "afterTestMethod")
      }
   }
}

private val events = mutableListOf<String>()

@Component
class LifecycleTestService {

   @BeforeTestExecution
   fun beforeTestExecution() {
      events.add("beforeTestExecution")
   }

   @AfterTestExecution
   fun afterTestExecution() {
      events.add("afterTestExecution")
   }

   @BeforeTestMethod
   fun beforeTestMethod() {
      events.add("beforeTestMethod")
   }

   @AfterTestMethod
   fun afterTestMethod() {
      events.add("afterTestMethod")
   }
}
