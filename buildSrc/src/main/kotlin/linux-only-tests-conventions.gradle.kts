// this convention disables CI tests unless we are running on the linux runner, to be used by modules
// that have no platform specific code that needs to be tested on all platforms. This speeds up builds
// on the slower macos/windows github action runners which bottleneck our builds

plugins {
   id("kotlin-conventions")
}

tasks.withType<AbstractTestTask>().configureEach {
   enabled = System.getenv("CI") != "true" || System.getenv("RUNNER_OS") == "Linux"
}
