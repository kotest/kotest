import Ci.snapshotVersion

object Ci {

   /**
    * The base version used for the release version.
    * For non release builds, `-SNAPSHOT` or `-LOCAL` will be appended.
    */
   private const val SNAPSHOT_BASE = "6.0.0"

   // The build number from GitHub Actions, if available, which is used to create a unique snapshot version.
   private val githubBuildNumber = System.getenv("GITHUB_RUN_NUMBER")

   /** Is the build currently running on CI. */
   private val isCI = System.getenv("CI").toBoolean()

   private val snapshotVersion = when (githubBuildNumber) {
      null -> "$SNAPSHOT_BASE-LOCAL"
      else -> "$SNAPSHOT_BASE.${githubBuildNumber}-SNAPSHOT"
   }

   /** The final release version. If specified, will override [snapshotVersion]. */
   private val releaseVersion = System.getenv("RELEASE_VERSION")?.ifBlank { null }

   // true if we are publishing to maven central or a similar repository
   // false if we are publishing to a local directory for testing purposes or publishing the gradle plugin
   val isRelease = releaseVersion != null

   val gradleRelease = System.getenv("GRADLE_RELEASE")?.ifBlank { null } ?: snapshotVersion

   /** The published version of Kotest dependencies. */
   val publishVersion = releaseVersion ?: snapshotVersion

   /**
    * Property to flag the build as JVM only, can be used to run checks on local machine much faster.
    */
   const val JVM_ONLY = "jvmOnly"
}
