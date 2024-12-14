package io.kotest.extensions.spring

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [(Components::class)])
@ApplyExtension(SpringAutowireConstructorExtension::class)
class SpringAutowiredConstructorTest(service: UserService) : WordSpec() {
  init {
    "SpringListener" should {
      "have autowired the service" {
        service.repository.findUser().name shouldBe "system_user"
      }
    }
  }
}
