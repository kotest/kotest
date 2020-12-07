---
id: inspectors
title: Inspectors
slug: inspectors.html
---






Inspectors allow us to test elements in a collection. They are extension functions for collections and arrays that test
that all, none or some of the elements pass the given assertions. For example, to test that a list of names contains
at least two elements which have a length of 7 or more, we can do this:


```kotlin
val xs = listOf("sam", "gareth", "timothy", "muhammad")
xs.forAtLeast(2) {
    it.shouldHaveMinLength(7)
}
```

Similarly, if we wanted to asset that *no* elements in a collection passed the assertions, we could do something like:

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





