package io.kotest.spring

import io.kotest.core.sourceRef
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [(Components::class)])
class SpringListenerTest : WordSpec() {

   override fun listeners() = listOf(SpringListener)

   @Autowired
   private var service: UserService? = null

   init {
      "SpringListener" should {
         "have autowired the service" {
            service?.repository?.findUser()?.name shouldBe "system_user"
         }
         "generate applicable method name for a root test" {
            SpringListener.methodName(TestCase(SpringListenerTest::class.toDescription().appendTest("0foo__!!55@#woo"),
               this@SpringListenerTest,
               {},
               sourceRef(),
               TestType.Test)) shouldBe "_0foo____55__woo"
         }
         "generate applicable method name for a nested test" {
            SpringListener.methodName(TestCase(SpringListenerTest::class.toDescription().appendTest("0foo__!!55@#woo").appendTest("wibble%%wobble"),
               this@SpringListenerTest,
               {},
               sourceRef(),
               TestType.Test)) shouldBe "_0foo____55__woo____wibble__wobble"
         }
      }
   }
}
