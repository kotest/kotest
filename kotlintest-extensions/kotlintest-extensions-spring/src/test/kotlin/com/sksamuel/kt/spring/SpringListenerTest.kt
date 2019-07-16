package com.sksamuel.kt.spring

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.spring.SpringListener
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
