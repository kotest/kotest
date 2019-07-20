package com.sksamuel.kt.spring

import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import io.kotlintest.spring.SpringListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [Components::class])
class SpringBootTestTest : FunSpec() {

    override fun listeners() = listOf(SpringListener)

    @Autowired
    lateinit var userService: UserService

    init {
        test("Should have correctly autowired user service with spring boot test") {
            userService.repository.findUser().name shouldBe "system_user"
        }
    }
}