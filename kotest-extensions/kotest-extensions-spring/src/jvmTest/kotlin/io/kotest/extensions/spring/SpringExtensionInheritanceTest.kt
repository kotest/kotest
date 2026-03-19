package io.kotest.extensions.spring

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

/**
 * Tests that @ApplyExtension on a base spec class is inherited by subclasses.
 * This addresses the issue where SpringExtension needs to be available for constructor injection.
 */
@SpringBootTest
@ContextConfiguration(classes = [Components::class])
@ApplyExtension(SpringExtension::class)
open class BaseSpringSpec : DescribeSpec()

@Suppress("ANNOTATION_WILL_BE_APPLIED_ALSO_TO_PROPERTY_OR_FIELD")
class SpringExtensionInheritanceTest(
   @Autowired private val service: UserService
) : BaseSpringSpec() {

   init {
      describe("SpringExtension inheritance") {
         it("should autowire constructor parameters from base class annotation") {
            service.repository.findUser().name shouldBe "system_user"
         }
      }
   }
}
