package io.kotest.engine.events

import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.core.listeners.Listener
import io.kotest.fp.Try
import io.kotest.mpp.log

/**
 * Invokes any afterProject functions from the given listeners.
 */
internal suspend fun List<Listener>.afterProject(): Try<List<AfterProjectListenerException>> = Try {
   log { "invokeAfterProject" }
   filterIsInstance<AfterProjectListener>()
      .resolveName()
      .map { it.first to Try { it.second.afterProject() } }
      .filter { it.second.isFailure() }
      .map {
         AfterProjectListenerException(
            it.first,
            (it.second as Try.Failure).error
         )
      }
}.mapFailure { AfterProjectListenerException("afterProjectsInvocation", it) }

/**
 * Invokes the beforeProject listeners.
 */
internal suspend fun List<Listener>.beforeProject(): Try<List<BeforeProjectListenerException>> = Try {
   log { "invokeBeforeProject" }
   filterIsInstance<BeforeProjectListener>()
      .resolveName()
      .map { it.first to Try { it.second.beforeProject() } }
      .filter { it.second.isFailure() }
      .map {
         BeforeProjectListenerException(
            it.first,
            (it.second as Try.Failure).error
         )
      }
}.mapFailure { BeforeProjectListenerException("beforeProjectsInvocation", it) }
