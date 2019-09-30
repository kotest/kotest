package com.sksamuel.kt.spring

import io.kotest.shouldBe
import io.kotest.specs.WordSpec
import io.kotest.spring.SpringListener
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
    }
  }
}
