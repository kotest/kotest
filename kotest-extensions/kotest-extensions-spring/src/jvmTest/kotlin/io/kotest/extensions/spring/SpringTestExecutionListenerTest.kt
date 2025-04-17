package io.kotest.extensions.spring

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.TestExecutionListener as SpringTestExecutionListener

@TestExecutionListeners(
   DummyTestExecutionListener::class,
   mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@SpringBootTest(classes = [Components::class])
class SpringTestExecutionListenerTest : FunSpec() {

   @Autowired
   lateinit var userService: UserService

   override val extensions = listOf(SpringExtension())

   init {
      test("Should autowire with spring listeners") {
         userService.repository.findUser()
      }

      test("Dummy test to test spring listener in afterSpecClass") {
         // Only here to verify counts are incremented
      }

      context("wrapped withData") {
         test("a") { }
      }

      afterProject {
         DummyTestExecutionListener.beforeTestClass shouldBe 1
         DummyTestExecutionListener.beforeTestMethod shouldBe 3
         DummyTestExecutionListener.beforeTestExecution shouldBe 3
         DummyTestExecutionListener.prepareTestInstance shouldBe 1
         DummyTestExecutionListener.afterTestExecution shouldBe 3
         DummyTestExecutionListener.afterTestMethod shouldBe 3
         DummyTestExecutionListener.afterTestClass shouldBe 1
      }
   }
}

class DummyTestExecutionListener : SpringTestExecutionListener {

   override fun beforeTestClass(testContext: TestContext) {
      beforeTestClass++
   }

   override fun beforeTestMethod(testContext: TestContext) {
      beforeTestMethod++
   }

   override fun beforeTestExecution(testContext: TestContext) {
      beforeTestExecution++
   }

   override fun prepareTestInstance(testContext: TestContext) {
      prepareTestInstance++
   }

   override fun afterTestExecution(testContext: TestContext) {
      afterTestExecution++
   }

   override fun afterTestMethod(testContext: TestContext) {
      afterTestMethod++
   }

   override fun afterTestClass(testContext: TestContext) {
      afterTestClass++
   }

   companion object {
      var beforeTestClass = 0
      var beforeTestMethod = 0
      var beforeTestExecution = 0
      var prepareTestInstance = 0
      var afterTestExecution = 0
      var afterTestMethod = 0
      var afterTestClass = 0
   }
}
