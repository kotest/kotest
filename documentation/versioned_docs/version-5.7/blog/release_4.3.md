Release 4.3.0
======

The Kotest team is pleased to announce the release of Kotest 4.3.0.

This blog covers some of the new features added in this release.
For the full list, see the [changelog](https://kotest.io/changelog/).



### New and improved data driven testing


Kotest has improved its data driven testing support, directly integrating into the framework.
This means it will now automatically generate individual test case entries.

As an example, lets test a function that returns true if the input values are valid [pythagorean triples](https://en.wikipedia.org/wiki/Pythagorean_triple).

```kotlin
fun isPythagTriple(a: Int, b: Int, c: Int): Boolean = a * a + b * b == c * c
```

We start by writing a data class that will hold each _row_ - a set of inputs.

```kotlin
data class PythagTriple(val a: Int, val b: Int, val c: Int)
```

Next we invoke the function `forAll` inside a test case, passing in one or more of these data classes, and a
lambda that performs some test logic for a given _row_.

```kotlin
context("Pythag triples tests") {
    forAll(
       PythagTriple(3, 4, 5),
       PythagTriple(6, 8, 10),
       PythagTriple(8, 15, 17),
       PythagTriple(7, 24, 25)
    ) { (a, b, c) ->
        isPythagTriple(a, b, c) shouldBe true
    }
}
```

Kotest will automatically generate a test case for each input row, as if you had manually written a separate test case for each.

![data test example output](images/datatest1.png)




### EnabledIf annotation on specs

It can be useful to avoid instantiating a spec entirely, and often we can do that via test tags. But if you want
to do this with some bespoke code, then the annotation `EnabledIf` has been added.

Annotate a spec with `EnabledIf`, passing in a class that extends from `EnabledCondition` and that condition
will be invoked at runtime to determine if the spec should be instantiated. The `EnabledCondition` implementation
must have a zero arg constructor.

For example, lets make a condition that only executes a test if it is midnight.

```kotlin
class EnabledIfMidnight : EnabledCondition {
   override fun enabled(specKlass: KClass<out Spec>): Boolean = LocalTime.now().hour == 0
}
```

And then attach that to a spec:

```kotlin
@EnabledIf(EnabledIfMidnight::class)
class EnabledIfTest : FunSpec() {
   init {
      test("tis midnight when the witches roam free") {
        // test here
      }
   }
}
```




### TestCase severity

Test case can be conditionally executed via test tags, and now also by severity levels.
The levels are BLOCKER, CRITICAL, NORMAL, MINOR, and TRIVIAL.

We can mark each test case with a severity level:

```kotlin
class MyTest : FunSpec() {
   init {
      test("very very important").config(severity = TestCaseSeverityLevel.CRITICAL) {
        // test here
      }
   }
}
```

Say we only want to execute tests that are CRITICAL or higher, we can execute with the system property `kotest.framework.test.severity=CRITICAL`

This can be useful if we have a huge test suite and want to run some tests first in a separate test run.

By default, all tests are executed.



### Disabling source references

Whenever a test case is created, Kotest creates a stack trace so that it can link back to the test case.
The stack trace contains the filename and line number which the Intellij Plugin uses to create links in the test window.
It calls these the _sourceref_.

If you have 1000s of tests and are encountering some slowdown when executing the full suite via gradle, you can now disable
the generation of these sourcerefs by setting the system property `kotest.framework.sourceref.disable=true`

Generally speaking, this is only of use if you have a huge test suite and mostly aimed at CI builds.



### Make engine dependency free

A test framework is one of the lowest levels of dependences in an ecosystem. As Kotest is used by many Kotlin libraries, a clash
can occur if Kotest and your project are using the same dependencies but with different versions.

It is beneficial then if Kotest has as few dependencies as possible. To this aim, 4.3.0 has seen the dependencies
for the Kotest framework reduced to just Classgraph (to scan for specs), Mordant (for console output), and opentest4j.





### Matchers return 'this' for easy chaining

In the opinion of this author, Kotest has the most comprehensive assertion support for Kotlin. Now they just became more convienient,
by allowing you to chain assertions together if you wish.

So, instead of
```kotlin
val employees: List<Employee> = ...
employees.shouldBeSorted()
employees.shouldHaveSize(4)
employees.shouldContain(Employee("Sam", "Chicago"))
```

You can now do
```kotlin
val employees: List<Employee> = ...
employees.shouldBeSorted()
          shouldHaveSize(4)
          shouldContain(Employee("Sam", "Chicago"))
```

Of course, this is entirely optional.







### Property test module for kotlinx datetime

Kotest's expansive property test generators now include ones for the incubating kotlinx datetime library.

Add the module `kotest-property-datetime` to your build. These generators are available for JVM and JS.

For example:

```kotlin
forAll(Arb.datetime(1987..1994)) { date ->
   isValidStarTrekTngSeason(date) shouldBe true
}
```






### Option to strip whitespace from test names

If you like to define test names over multiple lines, Kotest will now strip out leading, trailing and repeated whitespace from test names.

For example, the following spec:

```kotlin
class MySpec : StringSpec() {
  init {
   """this is a
      test spanning multiple lines""" { }
  }
}
```

Would normally be output as `this is a      test spanning multiple lines`

By setting the configuration object `removeTestNameWhitespace` to true, this would instead by output as `this is a test spanning multiple lines`





### Thanks

Huge thanks to all who contributed to this release (includes commits since v4.2.0 tag):

AJ Alt, Alex Facciorusso, Ashish Kumar Joy, J Phani Mahesh, Jasper de Vries, Javier Segovia Córdoba,
Josh Graham, KeremAslan, Leonardo Colman, Michał Sikora, Mitchell Yuwono, Neenad Ingole, Rick Busarow,
SergKhram, Sergei Khramkov, crazyk2, sksamuel
