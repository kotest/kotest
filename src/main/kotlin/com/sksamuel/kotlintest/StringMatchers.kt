package com.sksamuel.kotlintest

interface StringMatchers {

  infix fun Have<String>.substring(substr: String): (String) -> Unit {
    return { string ->
      if (!value.contains(substr))
        throw TestFailedException("String does not have substring $substr")
    }
  }

  infix fun Start<String>.with(prefix: String): (String) -> Unit {
    return { string ->
      if (!value.startsWith(prefix))
        throw TestFailedException("String does not start with $prefix but with ${value.take(prefix.length)}")
    }
  }

  infix fun End<String>.with(suffix: String): (String) -> Unit {
    return { string ->
      if (!value.endsWith(suffix))
        throw TestFailedException("String does not end with $suffix but with ${value.takeLast(suffix.length)}")
    }
  }
}