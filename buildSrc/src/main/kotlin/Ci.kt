object Ci {

   // this is the version used for building snapshots
   // .buildnumber-snapshot will be appended
   private const val snapshotBase = "5.8.999"

   private val githubBuildNumber = System.getenv("GITHUB_RUN_NUMBER")

   private val snapshotVersion = when (githubBuildNumber) {
      null -> "$snapshotBase-LOCAL"
      else -> "$snapshotBase.${githubBuildNumber}-SNAPSHOT"
   }

   private val snapshotGradleVersion = when (githubBuildNumber) {
      null -> "$snapshotBase-LOCAL"
      else -> "$snapshotBase.${githubBuildNumber}"
   }

   private val releaseVersion = System.getenv("RELEASE_VERSION")

   val isRelease = releaseVersion != null
   val publishVersion = releaseVersion ?: snapshotVersion
   val gradleVersion = releaseVersion ?: snapshotGradleVersion

   /**
    * Property to flag the build as JVM only, can be used to run checks on local machine much faster.
    */
   const val JVM_ONLY = "jvmOnly"
}
