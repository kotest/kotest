package io.kotest.assertions.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

// we need to make it public because inline functions use it
val mapper by lazy { ObjectMapper().registerKotlinModule() }
