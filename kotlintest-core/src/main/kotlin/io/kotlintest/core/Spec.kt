package io.kotlintest.core

interface Spec {

  /**
   * Intercepts the call of the spec class.
   *
   * Override this function if you wish to control the way each spec
   * is executed.
   *
   * This means this interceptor will be called once, before any of the
   * testcases in the spec are executed.
   *
   * To continue execution of this spec class, you must invoke the spec
   * function. If you don't want to continue with the execution of the spec,
   * then do not invoke the spec function.
   */
  fun interceptSpec(spec: () -> Unit) {
    spec()
  }

  /**
   * Intercepts the call of each test case.
   *
   * Override this function if you wish to control the way each test
   * case is executed.
   *
   * Don't forget to call `test()` in the body of this method.
   * Otherwise the test case will never be executed.
   */
  fun interceptTestCase(context: TestCaseContext, test: () -> Unit) {
    test()
  }
}