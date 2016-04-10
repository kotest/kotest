package io.kotlintest

import io.kotlintest.matchers.Matchers
import org.junit.runner.RunWith
import java.util.*

@RunWith(KTestJUnitRunner::class)
abstract class TestBase : Matchers {

  val root = TestSuite(this.javaClass.simpleName, ArrayList<TestSuite>(), ArrayList<TestCase>())

  fun beforeAll(): Unit {
  }

  fun afterAll(): Unit {
  }
}