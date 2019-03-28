package io.kotlintest.runner.js

import io.kotlintest.Spec

class TestRunner(specs: List<Spec>) {
  constructor(vararg specs: Spec) : this(specs.asList())

  init {

    console.log("Starting tests")

    specs.forEach { spec ->
      console.log("Executing spec $spec")
    }

    console.log("Completed tests")
  }
}