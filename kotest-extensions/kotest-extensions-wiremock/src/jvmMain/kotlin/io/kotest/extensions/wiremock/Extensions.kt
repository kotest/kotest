package io.kotest.extensions.wiremock

import com.github.tomakehurst.wiremock.WireMockServer
import io.kotest.core.listeners.TestListener

/**
 * Extension method to convert a wiremock into [WiremockPerSpecListener], which starts the given wiremock server
 * before every test and stop that after every test.
 * */
fun <T : WireMockServer> T.listenerPerSpec(): TestListener = WiremockPerSpecListener(this)

/**
 * Extension method to convert a wiremock into [WiremockPerTestListener], which starts the given wiremock server
 * before spec execute first test and stop that after spec completes.
 * */
fun <T : WireMockServer> T.listenerPerTest(): TestListener = WiremockPerTestListener(this)
