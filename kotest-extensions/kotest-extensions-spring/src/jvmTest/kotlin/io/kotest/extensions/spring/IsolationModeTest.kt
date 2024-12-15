package io.kotest.extensions.spring

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [(Components::class)])
@ApplyExtension(SpringTestExtension::class)
class IsolationModeTest : WordSpec() {

   override fun isolationMode() = IsolationMode.InstancePerTest

   @Autowired
   private lateinit var service: UserService

   init {
      "SpringExtension" should {
         "have autowired the service" {
            service.repository.findUser().name shouldBe "system_user"
         }
         "have autowired the service for another instance" {
            service.repository.findUser().name shouldBe "system_user"
         }
      }
   }
}
