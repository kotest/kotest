package com.sksamuel.kt.quarkus

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.quarkus.test.junit.QuarkusTest
import javax.inject.Inject

@QuarkusTest
class QuarkusListenerTest : WordSpec() {
   @Inject
   var service: UserService? = null

   init {
      "QuarkusListener" should {
         "have injected the service" {
            service.shouldNotBeNull()
         }
      }
   }
}
