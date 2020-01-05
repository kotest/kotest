package io.kotest.runner.jvm

import io.kotest.core.Project
import io.kotest.core.fp.Try
import io.kotest.core.fp.success
import io.kotest.core.specs.*
import io.kotest.specs.displayName
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

fun SpecContainer.instantiate(): Try<Spec> = when (this) {
   is SpecContainer.ValueSpec -> this.spec.value.success()
   is SpecContainer.ClassSpec -> this.kclass.instantiate().map { builder ->
      val spec = Spec(
         name = this.kclass.java.displayName(),
         configure = {},
         tests = builder.testCases(),
         beforeTest = builder.beforeTest,
         afterTest = builder.afterTest,
         beforeAll = builder.beforeAll,
         afterAll = builder.afterAll,
         isolationMode = builder.isolationMode,
         testCaseOrder = builder.testCaseOrder,
         tags = builder.tags,
         assertionMode = builder.assertionMode,
         listeners = builder.listeners,
         extensions = builder.extensions
      )
      builder.includes.fold(spec) { acc, op -> acc + op }
   }
}

fun KClass<out SpecBuilder>.instantiate(): Try<SpecBuilder> = Try {
   val initial: SpecBuilder? = null
   val instance = Project.constructorExtensions()
      .fold(initial) { spec, ext -> spec ?: ext.instantiate(this) } ?: this.createInstance()
   // after the class is created we no longer allow new top level tests to be added
   instance.acceptingTopLevelRegistration = false
   instance
}
