package com.sksamuel.kotlintest

import io.kotlintest.Tag

fun isTravis() = System.getenv("TRAVIS") == "true"
fun isAppveyor() = System.getenv("APPVEYOR") == "True"
fun isCI() = isTravis() || isAppveyor()

object AppveyorTag : Tag()
object TravisTag : Tag()