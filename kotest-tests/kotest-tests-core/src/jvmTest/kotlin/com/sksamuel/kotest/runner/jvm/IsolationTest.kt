package com.sksamuel.kotest.runner.jvm

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argThat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.then
import io.kotest.core.spec.description
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.engine.IsolationTestEngineListener
import io.kotest.core.engine.TestEngineListener

class IsolationTestEngineListenerTest : WordSpec({

   "IsolationTestEngineListener" should {
      "only notify for the running test" {

         val mock = mock<TestEngineListener> {}
         val listener = IsolationTestEngineListener(mock)

         val spec1 = IsolationTestSpec1()
         val spec2 = IsolationTestSpec2()
         val spec3 = IsolationTestSpec3()

         listener.specStarted(spec1::class)
         listener.specInstantiated(spec1)
         listener.specInstantiated(spec2)
         listener.specInstantiated(spec3)

         then(mock).should()
            .specInstantiated(argThat { this::class.description().fullName() == "com.sksamuel.kotest.runner.jvm.IsolationTestSpec1" })
         then(mock).should(never())
            .specInstantiated(argThat { this::class.description().fullName() == "com.sksamuel.kotest.runner.jvm.IsolationTestSpec2" })
         then(mock).should(never())
            .specInstantiated(argThat { this::class.description().fullName() == "com.sksamuel.kotest.runner.jvm.IsolationIsolationTestSpec3" })
      }
      "run queued callbacks for a single next spec when current spec completes" {

         val mock = mock<TestEngineListener> {}
         val listener = IsolationTestEngineListener(mock)

         val spec1 = IsolationTestSpec1()
         val spec2 = IsolationTestSpec2()
         val spec3 = IsolationTestSpec3()

         listener.specStarted(spec1::class)
         listener.specInstantiated(spec1)
         listener.specStarted(spec2::class)
         listener.specInstantiated(spec2)
         listener.specFinished(spec3::class, null, emptyMap())

         then(mock).should().specStarted(argThat { this.simpleName == spec1::class.simpleName })
         then(mock).should()
            .specInstantiated(argThat { this::class.description().fullName() == "com.sksamuel.kotest.runner.jvm.IsolationTestSpec1" })
         then(mock).should(never())
            .specInstantiated(argThat { this::class.description().fullName() == "com.sksamuel.kotest.runner.jvm.IsolationTestSpec2" })

         listener.specFinished(spec1::class, null, emptyMap())
         then(mock).should().specFinished(argThat { this.simpleName == spec1::class.simpleName }, anyOrNull(), any())
         then(mock).should().specStarted(argThat { this.simpleName == spec2::class.simpleName })
         then(mock).should()
            .specInstantiated(argThat { this::class.description().fullName() == "com.sksamuel.kotest.runner.jvm.IsolationTestSpec2" })

         then(mock).should(never())
            .specInstantiated(argThat { this::class.description().fullName() == "com.sksamuel.kotest.runner.jvm.IsolationTestSpec3" })
      }
   }
})

class IsolationTestSpec1 : WordSpec()
class IsolationTestSpec2 : WordSpec()
class IsolationTestSpec3 : WordSpec()
