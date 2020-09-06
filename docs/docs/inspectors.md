Inspectors
===========


Inspectors allow us to test elements in a collection. They are extension functions for collections and arrays that test
that all, none or some of the elements pass the given assertions. For example, to test that all elements in a collection
contain an underscore and start with "aa" we could do:

```kotlin
class StringSpecExample : StringSpec({
  "your test case" {
    val xs = listOf("aa_1", "aa_2", "aa_3")
    xs.forAll {
      it.shouldContain("_")
      it.shouldStartWith("aa")
    }
  }
})
```

Similarly, if we wanted to asset that *no* elements in a collection passed the assertions, we can do:

```kotlin
xs.forNone {
  it.shouldContain("x")
  it.shouldStartWith("bb")
}
```

The full list of inspectors are:

* `forAll` which asserts every element passes the assertions
* `forNone` which asserts no element passes
* `forOne` which asserts only a single element passed
* `forAtMostOne` which asserts that either 0 or 1 elements pass
* `forAtLeastOne` which asserts that 1 or more elements passed
* `forAtLeast(k)` which is a generalization that k or more elements passed
* `forAtMost(k)` which is a generalization that k or fewer elements passed
* `forAny` which is an alias for `forAtLeastOne`
* `forSome` which asserts that between 1 and n-1 elements passed. Ie, if NONE pass or ALL pass then we consider that a failure.
* `forExactly(k)` which is a generalization that exactly k elements passed. This is the basis for the implementation of the other methods





