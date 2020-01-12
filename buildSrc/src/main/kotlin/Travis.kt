object Travis {
   val isTravis = System.getenv("TRAVIS") == "true"
   val travisBuildNumber: String = System.getenv("TRAVIS_BUILD_NUMBER") ?: "0"
   val isReleaseVersion = !isTravis
}
