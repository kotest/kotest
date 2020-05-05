package io.kotest.assertions.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

private val mapper by lazy { ObjectMapper().registerKotlinModule() }

@PublishedApi
internal val publishedMapper
    get() = mapper
