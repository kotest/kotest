package com.sksamuel.kt.quarkus

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.quarkus.test.junit.QuarkusTest
import javax.inject.Inject

@QuarkusTest
class QuarkusUserServiceTest : FunSpec() {
   @Inject
   lateinit var userService: UserService

   init {
      test("Should have correctly injected the user service") {
         userService.repository.findUser().name shouldBe "system_user"
      }
   }
}
