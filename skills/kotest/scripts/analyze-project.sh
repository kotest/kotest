#!/bin/sh
#
# analyze-project.sh - Analyze a Kotlin project for Kotest setup and usage
#
# Usage: ./analyze-project.sh [PROJECT_ROOT]
#        Defaults to current directory if PROJECT_ROOT is not specified.

set -e

PROJECT_ROOT="${1:-.}"
PROJECT_ROOT="$(cd "$PROJECT_ROOT" && pwd)"

TMP_DIR="$(mktemp -d)"
trap 'rm -rf "$TMP_DIR"' EXIT
BUILD_LIST="$TMP_DIR/build-files"
TEST_LIST="$TMP_DIR/test-files"

TOML_FILE="$PROJECT_ROOT/gradle/libs.versions.toml"

# Build scripts (incl. buildSrc / convention plugins), excluding generated / cache directories
find "$PROJECT_ROOT" \( -name build -o -name .gradle -o -name node_modules -o -name .git \) -prune -o \
   \( -name "*.gradle.kts" -o -name "*.gradle" \) \
   -type f -print | sort > "$BUILD_LIST"

# Kotlin test sources: src/test, src/androidTest, and every KMP *Test source set (commonTest, jvmTest, ...)
find "$PROJECT_ROOT" \( -name build -o -name .gradle -o -name node_modules -o -name .git \) -prune -o \
   -name "*.kt" -type f -print | grep -E '/src/([A-Za-z0-9]*Test|test)/' | sort > "$TEST_LIST" || true

# grep the build files; prints matching lines (empty if none)
grep_build() {
   if [ -s "$BUILD_LIST" ]; then
      tr '\n' '\0' < "$BUILD_LIST" | xargs -0 grep -hE "$1" 2>/dev/null || true
   fi
}

# number of test files matching an extended regex
count_tests() {
   if [ -s "$TEST_LIST" ]; then
      tr '\n' '\0' < "$TEST_LIST" | xargs -0 grep -lE "$1" 2>/dev/null | wc -l | tr -d ' '
   else
      echo 0
   fi
}

# succeeds if version $1 (x.y[.z]) is lower than major $2 / minor $3
version_lt() {
   echo "$1" | awk -F. -v maj="$2" -v min="$3" '{ if (($1 + 0) < maj || (($1 + 0) == maj && ($2 + 0) < min)) exit 0; exit 1 }'
}

rel() { echo "$1" | sed "s|$PROJECT_ROOT/||"; }

echo "========================================"
echo " Kotest Project Analysis"
echo "========================================"
echo ""
echo "Project root: $PROJECT_ROOT"
echo ""

# --- Versions ---
echo "----------------------------------------"
echo " Versions"
echo "----------------------------------------"

KOTLIN_VERSION=""
KOTEST_VERSION=""
if [ -f "$TOML_FILE" ]; then
   KOTLIN_VERSION=$(grep -E '^[[:space:]]*kotlin([-_](version|gradle-plugin|plugin|compile-version))?[[:space:]]*=' "$TOML_FILE" \
      | head -1 | sed -E 's/[^"]*"([^"]+)".*/\1/')
   KOTEST_VERSION=$(grep -E '^[[:space:]]*kotest([-_]version)?[[:space:]]*=' "$TOML_FILE" | head -1 | sed -E 's/[^"]*"([^"]+)".*/\1/')
fi
if [ -z "$KOTLIN_VERSION" ]; then
   KOTLIN_VERSION=$(grep_build 'kotlin\("(jvm|multiplatform|android|js)"\)\.?[[:space:]]*version|org\.jetbrains\.kotlin\.[a-z.-]+"?\)?\.?[[:space:]]*version' \
      | head -1 | sed -E 's/.*version[[:space:](]*"([^"]+)".*/\1/')
fi
if [ -z "$KOTEST_VERSION" ]; then
   KOTEST_VERSION=$(grep_build 'io\.kotest:kotest-[a-z0-9-]+:[0-9]|id[( ]*"io\.kotest"\)?\.?[[:space:]]*version' \
      | grep -oE '(:|version[[:space:](]*")[0-9]+\.[0-9]+[0-9A-Za-z.-]*' | head -1 | sed -E 's/^(:|version[[:space:](]*")//')
fi

echo "  Kotlin: ${KOTLIN_VERSION:-not detected}"
echo "  Kotest: ${KOTEST_VERSION:-not detected}"

WRAPPER_PROPS="$PROJECT_ROOT/gradle/wrapper/gradle-wrapper.properties"
if [ -f "$WRAPPER_PROPS" ]; then
   GRADLE_VERSION=$(grep 'distributionUrl' "$WRAPPER_PROPS" | sed 's|.*gradle-||' | sed 's|-.*||')
   echo "  Gradle: $GRADLE_VERSION"
else
   echo "  Gradle: wrapper not found"
fi
echo ""

# --- Kotest Dependencies ---
echo "----------------------------------------"
echo " Kotest References"
echo "----------------------------------------"
KOTEST_FOUND="no"
if [ -f "$TOML_FILE" ] && grep -qi 'kotest' "$TOML_FILE"; then
   echo "  gradle/libs.versions.toml:"
   grep -i 'kotest' "$TOML_FILE" | head -15 | sed 's/^/    /'
   KOTEST_FOUND="yes"
fi
SHOWN=0
while IFS= read -r BUILD_FILE; do
   REFS=$(grep -n 'kotest' "$BUILD_FILE" 2>/dev/null || true)
   if [ -n "$REFS" ]; then
      KOTEST_FOUND="yes"
      SHOWN=$((SHOWN + 1))
      if [ "$SHOWN" -le 10 ]; then
         echo "  $(rel "$BUILD_FILE"):"
         echo "$REFS" | head -5 | sed 's/^/    /'
      fi
   fi
done < "$BUILD_LIST"
[ "$SHOWN" -gt 10 ] && echo "  ... and $((SHOWN - 10)) more build files referencing kotest"
[ "$KOTEST_FOUND" = "no" ] && echo "  No kotest references found"
echo ""

# --- Targets & Plugins ---
echo "----------------------------------------"
echo " Targets & Plugins"
echo "----------------------------------------"
HAS_KMP="no"; HAS_JVM="no"; HAS_KSP="no"; HAS_KOTEST_PLUGIN="no"; HAS_JUNIT_PLATFORM="no"
[ -n "$(grep_build 'kotlin\("multiplatform"\)|org\.jetbrains\.kotlin\.multiplatform|kotlinMultiplatform|libs\.plugins\.kotlin\.multiplatform')" ] && HAS_KMP="yes"
[ -n "$(grep_build 'kotlin\("jvm"\)|org\.jetbrains\.kotlin\.jvm|kotlin\("android"\)|org\.jetbrains\.kotlin\.android|com\.android\.(application|library)|^[[:space:]]*jvm\(|libs\.plugins\.kotlin\.(jvm|android)')" ] && HAS_JVM="yes"
[ -n "$(grep_build 'com\.google\.devtools\.ksp|libs\.plugins\.ksp')" ] && HAS_KSP="yes"
[ -n "$(grep_build "id[( ]*[\"']io\.kotest[\"']|libs\.plugins\.kotest")" ] && HAS_KOTEST_PLUGIN="yes"
[ -n "$(grep_build 'useJUnitPlatform')" ] && HAS_JUNIT_PLATFORM="yes"
echo "  Multiplatform:          $HAS_KMP"
echo "  JVM / Android target:   $HAS_JVM"
echo "  KSP plugin:             $HAS_KSP"
echo "  Kotest Gradle plugin:   $HAS_KOTEST_PLUGIN"
echo "  useJUnitPlatform():     $HAS_JUNIT_PLATFORM"
echo ""

# --- Other Test Frameworks ---
echo "----------------------------------------"
echo " Other Test Frameworks"
echo "----------------------------------------"
HAS_JUNIT4="no"; HAS_JUNIT5="no"; HAS_TESTNG="no"; HAS_SPEK="no"
[ -n "$(grep_build 'junit:junit|junit4')" ] && HAS_JUNIT4="yes"
[ -n "$(grep_build 'junit-jupiter|org\.junit\.jupiter')" ] && HAS_JUNIT5="yes"
[ -n "$(grep_build 'testng')" ] && HAS_TESTNG="yes"
[ -n "$(grep_build 'spek')" ] && HAS_SPEK="yes"
echo "  JUnit 4: $HAS_JUNIT4   JUnit 5 (Jupiter): $HAS_JUNIT5   TestNG: $HAS_TESTNG   Spek: $HAS_SPEK"
echo ""

# --- ProjectConfig ---
echo "----------------------------------------"
echo " ProjectConfig"
echo "----------------------------------------"
APC=$( [ -s "$TEST_LIST" ] && tr '\n' '\0' < "$TEST_LIST" | xargs -0 grep -lE '(class|object)[[:space:]]+[A-Za-z0-9_]+[[:space:]]*:[[:space:]]*AbstractProjectConfig[[:space:]]*\(' 2>/dev/null || true )
if [ -n "$APC" ]; then
   echo "$APC" | head -10 | while IFS= read -r f; do echo "  $(rel "$f")"; done
   N=$(echo "$APC" | grep -c .)
   [ "$N" -gt 10 ] && echo "  ... and $((N - 10)) more"
else
   echo "  No AbstractProjectConfig subclass found in test sources"
fi
echo ""

# --- Test Files ---
echo "----------------------------------------"
echo " Test Files"
echo "----------------------------------------"
TEST_COUNT=$(grep -c . "$TEST_LIST" || true)
echo "  Kotlin files in test source sets: $TEST_COUNT"
if [ "$TEST_COUNT" -gt 0 ]; then
   echo ""
   echo "  Kotest spec styles (files):"
   for STYLE in FunSpec DescribeSpec BehaviorSpec StringSpec FreeSpec WordSpec FeatureSpec ExpectSpec ShouldSpec; do
      printf '    %-13s %s\n' "$STYLE:" "$(count_tests ":[[:space:]]*$STYLE[[:space:]]*[({]")"
   done
   echo ""
   echo "  Files with @Test (JUnit/TestNG):       $(count_tests '@Test\b')"
   echo "  Files using Kotest assertions:         $(count_tests 'import io\.kotest\.(matchers|assertions)')"
   echo "  Files using property testing:          $(count_tests 'import io\.kotest\.(property|permutations)')"
   echo "  Files using withData (5.x-style):      $(count_tests '\bwithData\(')"
fi
echo ""

# --- Summary ---
echo "========================================"
echo " Summary"
echo "========================================"
if [ -n "$KOTLIN_VERSION" ] && version_lt "$KOTLIN_VERSION" 2 2; then
   echo "  ⚠ Kotlin $KOTLIN_VERSION < 2.2 — Kotest 6.x requires Kotlin 2.2+ (stay on 5.x or upgrade Kotlin)"
fi
if [ -n "$KOTEST_VERSION" ] && version_lt "$KOTEST_VERSION" 6 0; then
   echo "  ⚠ Kotest $KOTEST_VERSION is 5.x or older — see KNOWN-ISSUES.md §11 for 6.x migration"
fi
LEGACY=$( { grep_build 'io\.kotest\.multiplatform|kotest-framework-datatest|kotest-assertions-api'; \
   [ -f "$TOML_FILE" ] && grep -E 'io\.kotest\.multiplatform|kotest-framework-datatest|kotest-assertions-api' "$TOML_FILE"; } || true )
if [ -n "$LEGACY" ]; then
   echo "  ⚠ 5.x-only plugin/artifacts found (io.kotest.multiplatform → io.kotest + KSP; datatest / assertions-api removed):"
   echo "$LEGACY" | sed 's/^/      /'
fi
if [ "$KOTEST_FOUND" = "yes" ]; then
   echo "  Kotest is already in use."
   if [ "$HAS_KMP" = "yes" ] && [ "$HAS_KSP" = "no" ]; then
      echo "  ⚠ KMP project without KSP — required for JS / Wasm / Native Kotest targets"
   fi
   if [ "$HAS_KMP" = "yes" ] && [ "$HAS_KOTEST_PLUGIN" = "no" ]; then
      echo "  ⚠ KMP project without the io.kotest Gradle plugin — required for JS / Wasm / Native targets"
   fi
   if [ "$HAS_JVM" = "yes" ] && [ "$HAS_JUNIT_PLATFORM" = "no" ]; then
      echo "  ⚠ JVM target but useJUnitPlatform() not found — required for JVM test discovery"
   fi
else
   echo "  Kotest is NOT currently in use."
   if [ "$HAS_JUNIT5" = "yes" ]; then
      echo "  → JUnit 5 detected. Path B (JUnit 5 → Kotest)"
   elif [ "$HAS_JUNIT4" = "yes" ]; then
      echo "  → JUnit 4 detected. Path B (JUnit 4 → Kotest)"
   elif [ "$HAS_TESTNG" = "yes" ]; then
      echo "  → TestNG detected. Path B (TestNG → Kotest)"
   elif [ "$HAS_SPEK" = "yes" ]; then
      echo "  → Spek detected. Path B (Spek → Kotest)"
   else
      echo "  → No test framework detected. Path A (setup from scratch)"
   fi
fi
echo ""
