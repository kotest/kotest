package com.sksamuel.kt.koin

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.ShouldSpec
import org.koin.dsl.module
import org.koin.test.check.checkModules

class CheckModulesTest : ShouldSpec({

   should("Enable check modules") {
      checkModules {
         modules(koinModule)
      }
   }

   should("Throw exception if module is incomplete") {
      shouldThrowAny {
         checkModules {
            modules(incompleteModule)
         }
      }
   }

})

private val incompleteModule = module {
   single { RepoUser(get()) }
}

private class RepoUser(
   private val repo: Repo
)

// Not defined in Koin
private class Repo
