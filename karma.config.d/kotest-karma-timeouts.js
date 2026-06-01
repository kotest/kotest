// Shared Karma configuration for all Kotest JS / WasmJS browser test tasks.
//
// CI runners are frequently slow or heavily loaded, and the JS/WasmJS test bundles are large, so the
// headless browser can sit idle for longer than Karma's default 30s `browserNoActivityTimeout` while a
// bundle compiles/loads. That produced spurious failures such as:
//   "Disconnected (0 times) , because no message in 30000 ms."
// Raise the disconnect / no-activity / capture timeouts (and allow a couple of disconnect retries) so a
// slow startup no longer fails the build. These only affect how long Karma waits — a genuinely failing
// or hanging test is still bounded by the Gradle test task timeout.
config.set({
   browserDisconnectTimeout: 120000,
   browserDisconnectTolerance: 3,
   browserNoActivityTimeout: 120000,
   pingTimeout: 120000,
   captureTimeout: 120000,
});
