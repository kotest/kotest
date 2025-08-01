package io.kotest.extensions.spring

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.core.descriptors.toDescriptor
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [Components::class])
class SpringExtensionTest : WordSpec() {

   override val extensions = listOf(SpringExtension())

   @Autowired
   private lateinit var service: UserService

   init {
      "SpringExtension" should {
         "have autowired the service" {
            service.repository.findUser().name shouldBe "system_user"
         }
         "make test context available in the coroutine context" {
            testContextManager().shouldNotBeNull()
         }
         "generate applicable method name for a root test" {
            SpringExtension().methodName(
               TestCase(
                  descriptor = SpringExtensionTest::class.toDescriptor()
                     .append("0foo__!!55@#woo"),
                  name = TestNameBuilder.builder("0foo__!!55@#woo").build(),
                  spec = this@SpringExtensionTest,
                  test = {},
                  source = SourceRef.None,
                  type = TestType.Test
               )
            ) shouldStartWith "_0foo____55__woo"
         }
         "generate applicable method name for a nested test" {
            SpringExtension().methodName(
               TestCase(
                  descriptor = SpringExtensionTest::class.toDescriptor()
                     .append("0foo__!!55@#woo")
                     .append("wibble%%wobble"),
                  name = TestNameBuilder.builder("wibble%%wobble").build(),
                  spec = this@SpringExtensionTest,
                  test = {},
                  source = SourceRef.None,
                  type = TestType.Test
               )
            ) shouldStartWith "wibble__wobble"
         }
      }
   }
}
