package com.sksamuel.kotest.example.javascript

import kotlin.js.RegExp

val socialRegex = RegExp("^\\d{3}-\\d{3}-\\d{4}$")

fun validateSocial(ssn: String): Boolean = socialRegex.test(ssn)
