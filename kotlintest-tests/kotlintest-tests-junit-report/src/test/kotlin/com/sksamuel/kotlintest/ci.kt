package com.sksamuel.kotlintest

fun isTravis() = System.getenv("TRAVIS") == "true"
fun isAppveyor() = System.getenv("APPVEYOR") == "True"
fun isCI() = isTravis() || isAppveyor()