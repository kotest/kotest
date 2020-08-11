package com.sksamuel.kotest.js

// to show that we can test js specific functions we just need a simple function in jsMain
fun now(): Long = js("Date.now()").unsafeCast<Long>()
