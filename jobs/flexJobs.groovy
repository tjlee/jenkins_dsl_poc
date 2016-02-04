import com.setdsl.ServerJobTemplate

/**
 * Flex build jobs:
 * - build_flex
 * - build_flex_with_unit_tests
 *
 * - build_flex_pull_request
 * - build_flex_pull_request_with_unit_tests
 */

// build_flex
new ServerJobTemplate(
        name: "build_flex",
        description: "Builds flex",
        gitHubCheckoutDir: "setretail10",
        gitHubOwnerAndProject: "crystalservice/setretail10",
        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
        antSourceDir: "setretail10/SetRetail10_Server_GUI",
        isToBuildFlex: true,
        isToPublishUnitTests: false
).build(this)

// build_flex_with_unit_tests
new ServerJobTemplate(
        name: "build_flex_with_unit_tests",
        description: "Builds flex with flex unit tests",
        gitHubCheckoutDir: "setretail10",
        gitHubOwnerAndProject: "crystalservice/setretail10",
        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
        antSourceDir: "setretail10/SetRetail10_Server_GUI",
        antBuildTestFile: "setretail10/SetRetail10_Server_GUI/build_tests.xml",
        isToBuildFlex: true
).build(this)

// build_flex_pull_request
new ServerJobTemplate(
        name: "build_flex_pull_request",
        description: "Builds pull request flex",
        gitHubCheckoutDir: "setretail10",
        gitHubOwnerAndProject: "crystalservice/setretail10",
        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
        antSourceDir: "setretail10/SetRetail10_Server_GUI",
        isToBuildFlex: true,
        isPullRequest: true,
        isToPublishUnitTests: false
).build(this)

// build_flex_pull_request_with_unit_tests
new ServerJobTemplate(
        name: "build_flex_pull_request_with_unit_tests",
        description: "Builds flex pull request with flex unit tests",
        gitHubCheckoutDir: "setretail10",
        gitHubOwnerAndProject: "crystalservice/setretail10",
        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
        antSourceDir: "setretail10/SetRetail10_Server_GUI",
        antBuildTestFile: "setretail10/SetRetail10_Server_GUI/build_tests.xml",
        isToBuildFlex: true,
        isPullRequest: true,
        isToPublishUnitTests: false
).build(this)