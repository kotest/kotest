package com.sksamuel.kt.spring

import io.kotest.shouldBe
import io.kotest.specs.FunSpec
import io.kotest.spring.SpringListener
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes = [Components::class])
@ActiveProfiles("test-profile")
class ActiveProfileSpringTest : FunSpec() {

    override fun listeners() = listOf(SpringListener)

    @Value("\${test-foo}")
    lateinit var testFoo: String

    init {
        test("Should load active profile properties correctly") {
            testFoo shouldBe "bar"
        }
    }

}