object Ci {

   /**
    * The base version used for the release version.
    *
    * `-SNAPSHOT` or `-LOCAL` will be appended.
    */
   private const val snapshotBase = "5.10.0"

   /** Is the build currently running on CI. */
   private val isCI = System.getenv("CI").toBoolean()

   private val snapshotVersion =
      snapshotBase + if (isCI) "-SNAPSHOT" else "-LOCAL"

   /** The final release version. If specified, will override [snapshotVersion]. */
   private val releaseVersion = System.getenv("RELEASE_VERSION")?.ifBlank { null }

   val isRelease = releaseVersion != null

   /** The published version of Kotest dependencies. */
   val publishVersion = releaseVersion ?: snapshotVersion

   /**
    * Property to flag the build as JVM only, can be used to run checks on local machine much faster.
    */
   const val JVM_ONLY = "jvmOnly"
}
