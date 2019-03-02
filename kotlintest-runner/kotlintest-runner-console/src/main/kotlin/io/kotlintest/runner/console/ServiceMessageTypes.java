//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.kotlintest.runner.console;

import org.jetbrains.annotations.NonNls;

public interface ServiceMessageTypes {
    @NonNls
    String PUBLISH_ARTIFACTS = "publishArtifacts";
    @NonNls
    String TEST_SUITE_STARTED = "testSuiteStarted";
    @NonNls
    String TEST_SUITE_FINISHED = "testSuiteFinished";
    @NonNls
    String TEST_STARTED = "testStarted";
    @NonNls
    String TEST_FINISHED = "testFinished";
    @NonNls
    String TEST_IGNORED = "testIgnored";
    @NonNls
    String TEST_STD_OUT = "testStdOut";
    @NonNls
    String TEST_STD_ERR = "testStdErr";
    @NonNls
    String TEST_FAILED = "testFailed";
    @NonNls
    String PROGRESS_MESSAGE = "progressMessage";
    @NonNls
    String PROGRESS_START = "progressStart";
    @NonNls
    String PROGRESS_FINISH = "progressFinish";
    @NonNls
    String BUILD_STATUS = "buildStatus";
    @NonNls
    String BUILD_NUMBER = "buildNumber";
    @NonNls
    String BUILD_PARAMETER = "buildParameter";
    @NonNls
    String BUILD_ENVIRONMENT = "buildEnvironment";
    @NonNls
    String BUILD_SET_PARAMETER = "setParameter";
    @NonNls
    String BUILD_STATISTIC_VALUE = "buildStatisticValue";
    @NonNls
    String TEST_NAVIGATION_INFO = "testNavigationInfo";
    @NonNls
    String BLOCK_OPENED = "blockOpened";
    @NonNls
    String BLOCK_CLOSED = "blockClosed";
    @NonNls
    String COMPILATION_STARTED = "compilationStarted";
    @NonNls
    String COMPILATION_FINISHED = "compilationFinished";
    @NonNls
    String MESSAGE = "message";
    @NonNls
    String INTERNAL_ERROR = "internalError";
}
