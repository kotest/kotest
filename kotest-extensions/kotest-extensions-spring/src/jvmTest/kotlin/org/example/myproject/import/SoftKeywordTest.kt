@file:Suppress("ktIdIsJavaKw", "MatchingDeclarationName")

package org.example.myproject.import

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.spring.Components
import io.kotest.extensions.spring.SpringExtension
import io.kotest.extensions.spring.UserService
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

class IllegalPackageNameTest : FunSpec() {
   init {
      test("should throw clear error on illegal package name") {
         @Suppress("MaxLineLength")
         shouldThrowAny {
            SpringExtension.intercept(this@IllegalPackageNameTest) {}
         }.message shouldBe "Spec package name cannot contain a java keyword: import,finally,catch,const,final,inner,protected,private,public"
      }
   }
}

@Suppress("UnusedPrivateClass")
@SpringBootTest
@ContextConfiguration(classes = [Components::class])
private class SoftKeywordTest(
   @Suppress("UNUSED_PARAMETER") service: UserService
) : StringSpec({
   extensions(SpringExtension)
   "empty test should always be green" {
   }
})
