package io.kotlintest.spring

import io.kotlintest.extensions.SpecInterceptor
import io.kotlintest.specs.WordSpec
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

  override fun specExtensions(): List<SpecInterceptor> = listOf(SpringSpecExtension)

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