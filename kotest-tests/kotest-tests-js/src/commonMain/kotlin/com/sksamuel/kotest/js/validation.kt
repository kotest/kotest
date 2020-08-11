package com.sksamuel.kotest.js

// ridiculously stupid email validation just to prove we can test common code
fun validateEmail(email: String): Boolean {
   return email.matches(".+@.+\\..+".toRegex())
}
