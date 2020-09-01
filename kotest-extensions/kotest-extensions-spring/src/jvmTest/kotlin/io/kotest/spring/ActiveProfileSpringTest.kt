package io.kotest.spring

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
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
