package io.kotlintest.runner.jvm

import java.util.concurrent.TimeUnit

class TestTimedOutException(val timeout: Long, val timeUnit: TimeUnit) : RuntimeException()