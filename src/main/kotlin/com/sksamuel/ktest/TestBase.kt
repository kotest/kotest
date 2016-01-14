package com.sksamuel.ktest

import java.util.*

abstract class TestBase {
  val root = TestSuite("root", ArrayList<TestSuite>(), ArrayList<TestCase>())
}