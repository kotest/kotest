package io.kotest.extensions.spring

import io.kotest.core.sourceRef
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.spring.Components
import io.kotest.spring.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [(Components::class)])
class SpringExtensionTest : WordSpec() {

   override fun extensions() = listOf(SpringExtension)

   @Autowired
   private var service: UserService? = null

   init {
      "SpringExtension" should {
         "have autowired the service" {
            service?.repository?.findUser()?.name shouldBe "system_user"
         }
         "make test context available in the coroutine context"{
            testContextManager().shouldNotBeNull()
         }
         "generate applicable method name for a root test" {
            SpringExtension.methodName(
               TestCase(
                  SpringExtensionTest::class.toDescription().appendTest("0foo__!!55@#woo"),
                  this@SpringExtensionTest,
                  {},
                  sourceRef(),
                  TestType.Test
               )
            ) shouldBe "_0foo____55__woo"
         }
         "generate applicable method name for a nested test" {
            SpringExtension.methodName(
               TestCase(
                  SpringExtensionTest::class.toDescription().appendTest("0foo__!!55@#woo").appendTest("wibble%%wobble"),
                  this@SpringExtensionTest,
                  {},
                  sourceRef(),
                  TestType.Test
               )
            ) shouldBe "_0foo____55__woo____wibble__wobble"
         }
      }
   }
}
