package io.kotest.extensions.spring

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [Components::class])
class SpringBootTestTest : FunSpec() {

   override val extensions = listOf(SpringExtension())

    @Autowired
    lateinit var userService: UserService

    init {
        test("Should have correctly autowired user service with spring boot test") {
            userService.repository.findUser().name shouldBe "system_user"
        }
    }
}
