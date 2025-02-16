package io.kotest.assertions.arrow.fx.coroutines

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.resource

class ResourceUnderTest {
  var count: Int = 0
  var isOpen: Boolean = false
    private set
  var isReleased: Boolean = false
    private set
  var isConfigured: Boolean = false
    private set

  fun configure(): Unit {
    isConfigured = true
  }

  fun asResource(): Resource<ResourceUnderTest> = resource {
    install(
      acquire = {
        isReleased = false
        isOpen = true
        count++
        this@ResourceUnderTest
      },
      release = { res, _ ->
        res.isOpen = false
        res.isConfigured = false
        res.isReleased = true
      }
    )
  }
}
