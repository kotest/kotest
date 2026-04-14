#!/bin/sh
#
# analyze-project.sh - Analyze a Kotlin project for Kotest setup and usage
#
# Usage: ./analyze-project.sh [PROJECT_ROOT]
#        Defaults to current directory if PROJECT_ROOT is not specified.

set -e

PROJECT_ROOT="${1:-.}"

# Resolve to absolute path
PROJECT_ROOT="$(cd "$PROJECT_ROOT" && pwd)"

echo "========================================"
echo " Kotest Project Analysis"
echo "========================================"
echo ""
echo "Project root: $PROJECT_ROOT"
echo ""

# --- Kotlin Version ---
echo "----------------------------------------"
echo " Kotlin Version"
echo "----------------------------------------"
TOML_FILE="$PROJECT_ROOT/gradle/libs.versions.toml"
if [ -f "$TOML_FILE" ]; then
    KOTLIN_VERSION=$(grep -E '^kotlin\s*=' "$TOML_FILE" | head -1 | sed 's/.*= *"//' | sed 's/".*//')
    if [ -n "$KOTLIN_VERSION" ]; then
        echo "  Kotlin version (from version catalog): $KOTLIN_VERSION"
    else
        echo "  Kotlin version not found in version catalog"
    fi
else
    echo "  WARNING: libs.versions.toml not found"
fi
echo ""

# --- Gradle Version ---
echo "----------------------------------------"
echo " Gradle Version"
echo "----------------------------------------"
WRAPPER_PROPS="$PROJECT_ROOT/gradle/wrapper/gradle-wrapper.properties"
if [ -f "$WRAPPER_PROPS" ]; then
    GRADLE_URL=$(grep 'distributionUrl' "$WRAPPER_PROPS" | sed 's/.*=//' | sed 's/\\//g')
    GRADLE_VERSION=$(echo "$GRADLE_URL" | sed 's|.*gradle-||' | sed 's|-.*||')
    echo "  Gradle version: $GRADLE_VERSION"
else
    echo "  WARNING: gradle-wrapper.properties not found"
fi
echo ""

# --- Kotest Detection ---
echo "----------------------------------------"
echo " Kotest Dependencies"
echo "----------------------------------------"

# Check version catalog for kotest
if [ -f "$TOML_FILE" ]; then
    KOTEST_ENTRIES=$(grep -i 'kotest' "$TOML_FILE" 2>/dev/null || true)
    if [ -n "$KOTEST_ENTRIES" ]; then
        echo "  Found in version catalog:"
        echo "$KOTEST_ENTRIES" | sed 's/^/    /'
    else
        echo "  Not found in version catalog"
    fi
fi

# Check build files for kotest dependencies
echo ""
echo "  Searching build files for kotest references..."
BUILD_FILES=$(find "$PROJECT_ROOT" -name "build.gradle.kts" -o -name "build.gradle" | grep -v '.gradle/' | grep -v 'build/' | sort)
KOTEST_FOUND="no"
for BUILD_FILE in $BUILD_FILES; do
    KOTEST_REFS=$(grep -n 'kotest' "$BUILD_FILE" 2>/dev/null || true)
    if [ -n "$KOTEST_REFS" ]; then
        REL_PATH=$(echo "$BUILD_FILE" | sed "s|$PROJECT_ROOT/||")
        echo "    $REL_PATH:"
        echo "$KOTEST_REFS" | sed 's/^/      /'
        KOTEST_FOUND="yes"
    fi
done
if [ "$KOTEST_FOUND" = "no" ]; then
    echo "    No kotest references found in build files"
fi
echo ""

# --- Existing Test Framework Detection ---
echo "----------------------------------------"
echo " Existing Test Frameworks"
echo "----------------------------------------"

HAS_JUNIT4="no"
HAS_JUNIT5="no"
HAS_TESTNG="no"
HAS_SPEK="no"

for BUILD_FILE in $BUILD_FILES; do
    if grep -q 'junit:junit\|junit4' "$BUILD_FILE" 2>/dev/null; then
        HAS_JUNIT4="yes"
    fi
    if grep -q 'junit-jupiter\|junit5\|junit-platform\|useJUnitPlatform' "$BUILD_FILE" 2>/dev/null; then
        HAS_JUNIT5="yes"
    fi
    if grep -q 'testng' "$BUILD_FILE" 2>/dev/null; then
        HAS_TESTNG="yes"
    fi
    if grep -q 'spek' "$BUILD_FILE" 2>/dev/null; then
        HAS_SPEK="yes"
    fi
done

echo "  JUnit 4:  $HAS_JUNIT4"
echo "  JUnit 5:  $HAS_JUNIT5"
echo "  TestNG:   $HAS_TESTNG"
echo "  Spek:     $HAS_SPEK"
echo ""

# --- JUnit Platform Configuration ---
echo "----------------------------------------"
echo " JUnit Platform Configuration"
echo "----------------------------------------"

HAS_JUNIT_PLATFORM="no"
for BUILD_FILE in $BUILD_FILES; do
    if grep -q 'useJUnitPlatform' "$BUILD_FILE" 2>/dev/null; then
        REL_PATH=$(echo "$BUILD_FILE" | sed "s|$PROJECT_ROOT/||")
        echo "  useJUnitPlatform() found in: $REL_PATH"
        HAS_JUNIT_PLATFORM="yes"
    fi
done
if [ "$HAS_JUNIT_PLATFORM" = "no" ]; then
    echo "  WARNING: useJUnitPlatform() not found — required for Kotest on JVM"
fi
echo ""

# --- ProjectConfig Detection ---
echo "----------------------------------------"
echo " ProjectConfig"
echo "----------------------------------------"

# Look for ProjectConfig files
PROJ_CONFIGS=$(find "$PROJECT_ROOT" -path "*/test*" -name "ProjectConfig.kt" 2>/dev/null | grep -v 'build/' || true)
if [ -n "$PROJ_CONFIGS" ]; then
    echo "  Found ProjectConfig files:"
    echo "$PROJ_CONFIGS" | sed "s|$PROJECT_ROOT/||" | sed 's/^/    /'
else
    echo "  No ProjectConfig.kt found in test sources"
fi

# Check for AbstractProjectConfig references
APC_REFS=$(grep -rl 'AbstractProjectConfig' "$PROJECT_ROOT" --include="*.kt" 2>/dev/null | grep -v 'build/' || true)
if [ -n "$APC_REFS" ]; then
    echo "  AbstractProjectConfig references:"
    echo "$APC_REFS" | sed "s|$PROJECT_ROOT/||" | sed 's/^/    /'
fi
echo ""

# --- Multiplatform Detection ---
echo "----------------------------------------"
echo " Multiplatform"
echo "----------------------------------------"

HAS_KMP="no"
for BUILD_FILE in $BUILD_FILES; do
    if grep -q 'kotlin.*multiplatform\|kotlinMultiplatform' "$BUILD_FILE" 2>/dev/null; then
        REL_PATH=$(echo "$BUILD_FILE" | sed "s|$PROJECT_ROOT/||")
        echo "  KMP plugin found in: $REL_PATH"
        HAS_KMP="yes"
    fi
done
if [ "$HAS_KMP" = "no" ]; then
    echo "  Not a multiplatform project"
fi

# Check for KSP plugin (needed for KMP Kotest)
HAS_KSP="no"
for BUILD_FILE in $BUILD_FILES; do
    if grep -q 'com.google.devtools.ksp\|ksp' "$BUILD_FILE" 2>/dev/null; then
        HAS_KSP="yes"
    fi
done
echo "  KSP plugin: $HAS_KSP"

# Check for Kotest Gradle plugin
HAS_KOTEST_PLUGIN="no"
for BUILD_FILE in $BUILD_FILES; do
    if grep -q 'id("io.kotest")\|id .io.kotest.' "$BUILD_FILE" 2>/dev/null; then
        HAS_KOTEST_PLUGIN="yes"
    fi
done
echo "  Kotest Gradle plugin: $HAS_KOTEST_PLUGIN"
echo ""

# --- Test File Statistics ---
echo "----------------------------------------"
echo " Test Files"
echo "----------------------------------------"

TEST_FILES=$(find "$PROJECT_ROOT" -path "*/test*" -name "*.kt" 2>/dev/null | grep -v 'build/' | sort)
TEST_COUNT=$(echo "$TEST_FILES" | grep -c '.' 2>/dev/null || echo "0")
echo "  Total test files (*.kt in test sources): $TEST_COUNT"

if [ "$TEST_COUNT" -gt 0 ]; then
    # Count Kotest spec usage
    FUNSPEC=$(grep -rl 'FunSpec' "$PROJECT_ROOT" --include="*.kt" 2>/dev/null | grep 'test' | grep -v 'build/' | wc -l | tr -d ' ')
    DESCRIBESPEC=$(grep -rl 'DescribeSpec' "$PROJECT_ROOT" --include="*.kt" 2>/dev/null | grep 'test' | grep -v 'build/' | wc -l | tr -d ' ')
    BEHAVIORSPEC=$(grep -rl 'BehaviorSpec' "$PROJECT_ROOT" --include="*.kt" 2>/dev/null | grep 'test' | grep -v 'build/' | wc -l | tr -d ' ')
    STRINGSPEC=$(grep -rl 'StringSpec' "$PROJECT_ROOT" --include="*.kt" 2>/dev/null | grep 'test' | grep -v 'build/' | wc -l | tr -d ' ')
    FREESPEC=$(grep -rl 'FreeSpec' "$PROJECT_ROOT" --include="*.kt" 2>/dev/null | grep 'test' | grep -v 'build/' | wc -l | tr -d ' ')
    WORDSPEC=$(grep -rl 'WordSpec' "$PROJECT_ROOT" --include="*.kt" 2>/dev/null | grep 'test' | grep -v 'build/' | wc -l | tr -d ' ')
    FEATURESPEC=$(grep -rl 'FeatureSpec' "$PROJECT_ROOT" --include="*.kt" 2>/dev/null | grep 'test' | grep -v 'build/' | wc -l | tr -d ' ')
    EXPECTSPEC=$(grep -rl 'ExpectSpec' "$PROJECT_ROOT" --include="*.kt" 2>/dev/null | grep 'test' | grep -v 'build/' | wc -l | tr -d ' ')
    SHOULDSPEC=$(grep -rl 'ShouldSpec' "$PROJECT_ROOT" --include="*.kt" 2>/dev/null | grep 'test' | grep -v 'build/' | wc -l | tr -d ' ')

    echo ""
    echo "  Kotest Spec Styles in Use:"
    echo "    FunSpec:      $FUNSPEC"
    echo "    DescribeSpec: $DESCRIBESPEC"
    echo "    BehaviorSpec: $BEHAVIORSPEC"
    echo "    StringSpec:   $STRINGSPEC"
    echo "    FreeSpec:     $FREESPEC"
    echo "    WordSpec:     $WORDSPEC"
    echo "    FeatureSpec:  $FEATURESPEC"
    echo "    ExpectSpec:   $EXPECTSPEC"
    echo "    ShouldSpec:   $SHOULDSPEC"

    # Count JUnit test annotations
    JUNIT_TESTS=$(grep -rl '@Test' "$PROJECT_ROOT" --include="*.kt" 2>/dev/null | grep 'test' | grep -v 'build/' | wc -l | tr -d ' ')
    echo ""
    echo "  Files with @Test annotations (JUnit/TestNG): $JUNIT_TESTS"

    # Count shouldBe usage
    SHOULDBE=$(grep -rl 'shouldBe' "$PROJECT_ROOT" --include="*.kt" 2>/dev/null | grep 'test' | grep -v 'build/' | wc -l | tr -d ' ')
    echo "  Files using Kotest assertions (shouldBe): $SHOULDBE"

    # Count property testing
    PROP_TESTS=$(grep -rl 'forAll\|checkAll\|Arb\.' "$PROJECT_ROOT" --include="*.kt" 2>/dev/null | grep 'test' | grep -v 'build/' | wc -l | tr -d ' ')
    echo "  Files using property testing: $PROP_TESTS"
fi
echo ""

# --- Summary ---
echo "========================================"
echo " Summary"
echo "========================================"
echo ""
if [ "$KOTEST_FOUND" = "yes" ]; then
    echo "  Kotest is already in use in this project."
    if [ "$HAS_KMP" = "yes" ] && [ "$HAS_KSP" = "no" ]; then
        echo "  ⚠ KMP project without KSP — KSP is required for non-JVM Kotest targets"
    fi
    if [ "$HAS_KMP" = "yes" ] && [ "$HAS_KOTEST_PLUGIN" = "no" ]; then
        echo "  ⚠ KMP project without Kotest Gradle plugin — required for non-JVM targets"
    fi
    if [ "$HAS_JUNIT_PLATFORM" = "no" ]; then
        echo "  ⚠ useJUnitPlatform() not found — required for JVM test discovery"
    fi
else
    echo "  Kotest is NOT currently in use."
    if [ "$HAS_JUNIT5" = "yes" ]; then
        echo "  → JUnit 5 detected. Migration path: Path B (JUnit 5 → Kotest)"
    elif [ "$HAS_JUNIT4" = "yes" ]; then
        echo "  → JUnit 4 detected. Migration path: Path B (JUnit 4 → Kotest)"
    elif [ "$HAS_TESTNG" = "yes" ]; then
        echo "  → TestNG detected. Migration path: Path B (TestNG → Kotest)"
    elif [ "$HAS_SPEK" = "yes" ]; then
        echo "  → Spek detected. Migration path: Path B (Spek → Kotest)"
    else
        echo "  → No test framework detected. Recommended: Path A (Setup from scratch)"
    fi
fi
echo ""

