package com.sksamuel.kt.koin

import org.koin.dsl.module

open class GenericRepository {
   fun foo() = "Bar"
}

open class GenericService(
   val repository: GenericRepository
) {

   fun foo() = repository.foo()
}

val koinModule = module {
   single { GenericService(get()) }
   single { GenericRepository() }
}
