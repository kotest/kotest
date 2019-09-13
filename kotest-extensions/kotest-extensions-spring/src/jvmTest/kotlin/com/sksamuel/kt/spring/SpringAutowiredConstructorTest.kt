package com.sksamuel.kt.spring

import io.kotest.shouldBe
import io.kotest.specs.WordSpec
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [(Components::class)])
class SpringAutowiredConstructorTest(service: UserService) : WordSpec() {
  init {
    "SpringListener" should {
      "have autowired the service" {
        service.repository.findUser().name shouldBe "system_user"
      }
    }
  }
}