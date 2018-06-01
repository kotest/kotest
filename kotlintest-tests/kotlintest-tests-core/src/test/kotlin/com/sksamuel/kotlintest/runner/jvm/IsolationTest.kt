package com.sksamuel.kotlintest.runner.jvm

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.then
import io.kotlintest.runner.jvm.IsolationTestEngineListener
import io.kotlintest.runner.jvm.TestEngineListener
import io.kotlintest.specs.WordSpec

class IsolationTestEngineListenerTest : WordSpec({

  "IsolationTestEngineListener" should {
    "only notify for the running test" {

      val mock = mock<TestEngineListener> {}
      val listener = IsolationTestEngineListener(mock)

      val spec1 = IsolationTestSpec1()
      val spec2 = IsolationTestSpec2()
      val spec3 = IsolationTestSpec3()

      listener.specCreated(spec1)
      listener.specCreated(spec2)
      listener.specCreated(spec3)

      then(mock).should().specCreated(argThat { this.description().fullName() == "IsolationTestSpec1" })
      then(mock).should(never()).specCreated(argThat { this.description().fullName() == "IsolationTestSpec2" })
      then(mock).should(never()).specCreated(argThat { this.description().fullName() == "IsolationTestSpec3" })
    }
    "run queued callbacks for a single next spec when current spec completes" {

      val mock = mock<TestEngineListener> {}
      val listener = IsolationTestEngineListener(mock)

      val spec1 = IsolationTestSpec1()
      val spec2 = IsolationTestSpec2()
      val spec3 = IsolationTestSpec3()

      listener.specCreated(spec1)
      listener.specCreated(spec2)
      listener.prepareSpec(spec2.description(), spec2::class)
      listener.specCreated(spec3)
      then(mock).should().specCreated(argThat { this.description().fullName() == "IsolationTestSpec1" })

      listener.completeSpec(spec1.description(), spec1::class, null)
      then(mock).should().specCreated(argThat { this.description().fullName() == "IsolationTestSpec2" })
      then(mock).should().prepareSpec(argThat { this.fullName() == "IsolationTestSpec2" }, any())

      then(mock).should(never()).specCreated(argThat { this.description().fullName() == "IsolationTestSpec3" })
    }
  }
})

class IsolationTestSpec1 : WordSpec()
class IsolationTestSpec2 : WordSpec()
class IsolationTestSpec3 : WordSpec()