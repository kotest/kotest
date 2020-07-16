package io.kotest.core.spec

/**
 * The annotation [JsTest] is intercepted by the kotlin.js framework adapter to generate tests.
 * It is simply an alias to kotlin.test.Test on the JS target.
 *
 * For each annotated function, the compiler will insert a "test" call, with the name of the function
 * in the kotlin source, along with a lambda of the function itself. These test calls are wrapped inside
 * a "suite" call which is named for the class name, and that is wrapped in a "suite" call with the name
 * taken from the package name.
 *
 * For example:
 *
 * suite('com.sksamuel.kotest.example.javascript', false, function () {
 *    suite('SsnTest', false, function () {
 *       test('kotestGenerateTests', false, function () {
 *          return (new SsnTest()).kotestGenerateTests();
 *       });
 *    });
 * });
 *
 * We use
 */
expect annotation class JsTest()

annotation class AutoScan
