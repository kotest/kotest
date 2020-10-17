package io.kotest.core.filter

interface SpecClassFilter: Filter {

}

annotation class SpecClassExecutionFilter(val filter: String)
