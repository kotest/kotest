package io.kotest.extensions.testcontainers.options

import org.testcontainers.containers.output.OutputFrame
import java.util.function.Consumer

data class ContainerExtensionConfig(
   val logConsumer: Consumer<OutputFrame> = StandardLogConsumer(LogTypes.NONE)
)
