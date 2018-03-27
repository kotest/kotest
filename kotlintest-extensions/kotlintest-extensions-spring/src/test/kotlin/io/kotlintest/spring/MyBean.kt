package io.kotlintest.spring

import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration

class MyBean

@Configuration
open class Classes {
  @Bean
  open fun myBean() = MyBean()
}

@ContextConfiguration(classes = [(Classes::class)])
class SpringTest : WordSpec() {

  override fun listeners() = listOf(SpringListener)

  @Autowired
  var bean: MyBean? = null

  init {
    "Spring Extension" should {
      "have wired up the bean" {
        bean shouldNotBe null
      }
    }
  }
}