package io.kotest.engine.test.logging

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.LogLevel
import io.kotest.core.test.TestContext

/**
 * Appends to the [TestContext] log, when the log level is set to [io.kotest.core.config.LogLevel.Trace].
 */
@ExperimentalKotest
fun TestContext.trace(message: LogFn) = logger?.maybeLog(message, LogLevel.Trace)

/**
 * Appends to the [TestLogger] reference to the [TestContext] log, when the log level is set to [io.kotest.core.config.LogLevel.Trace]
 */
@ExperimentalKotest
fun TestLogger.trace(message: LogFn) = maybeLog(message, LogLevel.Trace)

/**
 * Appends to the [TestContext] log, when the log level is set to [io.kotest.core.config.LogLevel.Debug].
 */
@ExperimentalKotest
fun TestContext.debug(message: LogFn) = logger?.maybeLog(message, LogLevel.Debug)

/**
 * Appends to the [TestLogger] reference to the [TestContext] log, when the log level is set to [io.kotest.core.config.LogLevel.Debug]
 */
@ExperimentalKotest
fun TestLogger.debug(message: LogFn) = maybeLog(message, LogLevel.Debug)

/**
 * Appends to the [TestContext] log, when the log level is set to [io.kotest.core.config.LogLevel.Info] or higher.
 */
@ExperimentalKotest
fun TestContext.info(message: LogFn) = logger?.maybeLog(message, LogLevel.Info)

/**
 * Appends to the [TestLogger] reference to the [TestContext] log, when the log level is set to [io.kotest.core.config.LogLevel.Info]
 */
@ExperimentalKotest
fun TestLogger.info(message: LogFn) = maybeLog(message, LogLevel.Info)

/**
 * Appends to the [TestContext] log, when the log level is [io.kotest.core.config.LogLevel.Warn] or higher.
 */
@ExperimentalKotest
fun TestContext.warn(message: LogFn) = logger?.maybeLog(message, LogLevel.Warn)

/**
 * Appends to the [TestLogger] reference to the [TestContext] log, when the log level is set to [io.kotest.core.config.LogLevel.Warn]
 */
@ExperimentalKotest
fun TestLogger.warn(message: LogFn) = maybeLog(message, LogLevel.Warn)

/**
 * Appends to the [TestContext] log, when the log level is set to [io.kotest.core.config.LogLevel.Error] or higher.
 */
@ExperimentalKotest
fun TestContext.error(message: LogFn) = logger?.maybeLog(message, LogLevel.Error)

/**
 * Appends to the [TestLogger] reference to the [TestContext] log, when the log level is set to [io.kotest.core.config.LogLevel.Error]
 */
@ExperimentalKotest
fun TestLogger.error(message: LogFn) = maybeLog(message, LogLevel.Error)
