plugins {
   id("kotlin-conventions")
}

val kotestSettings = extensions.getByType<KotestBuildLogicSettings>()

kotlin {
   /**
    * Macos runners have limited CPU resources as they are expensive, so github (very reasonably) limits
    * concurrency on them for open source projects. We therefore only run these targets on master builds,
    * or on non-CI builds. Since the chances of a PR breaking watch/tv/ios but working on macos is pretty low,
    * we get to speed up the builds considerably, and worst case is we'll have a green PR build but a red
    * master build after merge. I think this trade off is acceptable.
    */
   if (!project.hasProperty(Ci.JVM_ONLY) && kotestSettings.enableKotlinNative.get() && Ci.shouldRunWatchTvIosModules) {
      watchosDeviceArm64()
   } else {
      // Make sure every project has at least one valid target, otherwise Kotlin compiler will complain
      jvm()
   }
}
