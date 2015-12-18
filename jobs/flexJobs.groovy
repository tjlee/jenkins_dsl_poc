import com.setdsl.ServerJobTemplateBuilder

/**
 * Flex build jobs:
 * - build_flex
 * - build_flex_with_unit_tests
 */

// build_flex
new ServerJobTemplateBuilder(
        name: "build_flex",
        description: "Builds flex",
        gitHubCheckoutDir: "setretail10",
        gitHubOwnerAndProject: "crystalservice/setretail10",
        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
        antSourceDir: "setretail10/SetRetail10_Server_GUI",
        antFlexSdkDir: "/opt/flexsdk",
        antAirSdkDir: "/opt/airsdk",
        isToBuildFlex: true
).build(this)

// build_flex_with_unit_tests
new ServerJobTemplateBuilder(
        name: "build_flex_with_unit_tests",
        description: "Builds flex with flex unit tests",
        gitHubCheckoutDir: "setretail10",
        gitHubOwnerAndProject: "crystalservice/setretail10",
        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
        antSourceDir: "setretail10/SetRetail10_Server_GUI",
        antFlexSdkDir: "/opt/flexsdk",
        antAirSdkDir: "/opt/airsdk",
        antBuildTestFile: "setretail10/SetRetail10_Server_GUI/build_tests.xml",
        isToBuildFlex: true
).build(this)
