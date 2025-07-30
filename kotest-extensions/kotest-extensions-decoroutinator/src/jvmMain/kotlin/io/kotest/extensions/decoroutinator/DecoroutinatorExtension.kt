package io.kotest.extensions.decoroutinator

import dev.reformator.stacktracedecoroutinator.jvm.DecoroutinatorJvmApi
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.spec.Spec

class DecoroutinatorExtension : BeforeProjectListener, BeforeSpecListener {

   override suspend fun beforeProject() {
      DecoroutinatorJvmApi.install()
   }

   override suspend fun beforeSpec(spec: Spec) {
      DecoroutinatorJvmApi.install()
   }
}
