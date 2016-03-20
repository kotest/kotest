package io.kotlintest

import org.junit.runner.RunWith
import java.util.*

@RunWith(KTestJUnitRunner::class)
abstract class TestBase {
  val root = TestSuite("root", ArrayList<TestSuite>(), ArrayList<TestCase>())
}