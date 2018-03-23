package io.kotlintest.spring4

import io.kotlintest.extensions.SpecExtension
import io.kotlintest.runner.junit5.specs.WordSpec
import io.kotlintest.shouldNotBe
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

  override fun specExtensions(): List<SpecExtension> = listOf(SpringSpecExtension)

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