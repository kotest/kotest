package io.kotest.assertions.arrow.fx.coroutines

import arrow.fx.coroutines.ExitCase
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.CancellationException

class ExitCaseTest : StringSpec({
  "shouldBeCancelled" {
    ExitCase.Cancelled(CancellationException("")).shouldBeCancelled()
  }

  "shouldBeCancelled(e)" {
    checkAll(Arb.string().map { CancellationException(it) }) { e ->
      ExitCase.Cancelled(e).shouldBeCancelled(e)
    }
  }

  "shouldBeCompleted" {
    ExitCase.Completed.shouldBeCompleted()
  }

  "shouldBeFailure" {
    checkAll(Arb.string().map(::RuntimeException)) { e ->
      ExitCase.Failure(e).shouldBeFailure()
    }
  }

  "shouldBeFailure(e)" {
    checkAll(Arb.string().map(::RuntimeException)) { e ->
      ExitCase.Failure(e).shouldBeFailure(e)
    }
  }
})
