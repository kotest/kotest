package io.kotlintest

import io.kotlintest.matchers.Matchers
import org.junit.runner.RunWith
import java.util.*

@RunWith(KTestJUnitRunner::class)
abstract class TestBase : Matchers {

  val root = TestSuite(this.javaClass.simpleName, ArrayList<TestSuite>(), ArrayList<TestCase>())

  open fun beforeAll(): Unit {
  }

  open fun afterAll(): Unit {
  }

  open fun beforeEach(): Unit {
  }

  open fun afterEach(): Unit {
  }
}