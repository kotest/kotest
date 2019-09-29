package com.sksamuel.kotest

import io.kotest.Tag

fun isTravis() = System.getenv("TRAVIS") == "true"
fun isAppveyor() = System.getenv("APPVEYOR") == "True"
fun isCI() = isTravis() || isAppveyor()

object AppveyorTag : Tag()
object TravisTag : Tag()