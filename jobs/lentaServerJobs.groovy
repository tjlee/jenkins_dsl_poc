import com.setdsl.ServerJobTemplateBuilder

/**
 * Lenta server build jobs:
 * - with flex
 * -- build_lenta_ear_flex
 * -- build_lenta_exe_flex
 * -- build_lenta_tgz_flex
 * -- build_lenta_iso_flex
 * - without flex
 * -- build_lenta_ear_without_flex
 *
 * - pull requests
 * -- build_lenta_pull_request_ear_without_flex
 */

// build_lenta_ear_flex
new ServerJobTemplateBuilder(
        name: "build_lenta_ear_flex",
        description: "Building lenta ear server with flex",
        gitHubCheckoutDir: "setretail10",
        gitHubOwnerAndProject: "crystalservice/setretail10",
        buildType: "ear",
        isToBuildFlex: true,
        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
        antSourceDir: "setretail10/SetRetail10_Server_GUI",
        antFlexSdkDir: "/opt/flexsdk",
        antAirSdkDir: "/opt/airsdk",
        clientType: "lenta"
).build(this)

// build_lenta_exe_flex
new ServerJobTemplateBuilder(
        name: "build_lenta_exe_flex",
        description: "Building lenta exe server with flex",
        gitHubCheckoutDir: "setretail10",
        gitHubOwnerAndProject: "crystalservice/setretail10",
        buildType: "exe",
        isToBuildFlex: true,
        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
        antSourceDir: "setretail10/SetRetail10_Server_GUI",
        antFlexSdkDir: "/opt/flexsdk",
        antAirSdkDir: "/opt/airsdk",
        clientType: "lenta"
).build(this)

// build_lenta_tgz_flex
new ServerJobTemplateBuilder(
        name: "build_lenta_tgz_flex",
        description: "Building lenta tgz server with flex",
        gitHubCheckoutDir: "setretail10",
        gitHubOwnerAndProject: "crystalservice/setretail10",
        buildType: "tgz",
        isToBuildFlex: true,
        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
        antSourceDir: "setretail10/SetRetail10_Server_GUI",
        antFlexSdkDir: "/opt/flexsdk",
        antAirSdkDir: "/opt/airsdk",
        clientType: "lenta"
).build(this)

// build_lenta_iso_flex
new ServerJobTemplateBuilder(
        name: "build_lenta_iso_flex",
        description: "Building lenta iso server with flex",
        gitHubCheckoutDir: "setretail10",
        gitHubOwnerAndProject: "crystalservice/setretail10",
        gitHubOwnerAndProjectLinuxSources: "crystalservice/setretail10linux",
        gitHubCheckoutDirLinuxSources: "setretail10linux",
        buildType: "iso",
        isToBuildFlex: true,
        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
        antSourceDir: "setretail10/SetRetail10_Server_GUI",
        antFlexSdkDir: "/opt/flexsdk",
        antAirSdkDir: "/opt/airsdk",
        clientType: "lenta"

).build(this)

// build_lenta_ear_without_flex
new ServerJobTemplateBuilder(
        name: "build_lenta_ear_without_flex",
        description: "Building lenta ear server without flex(empty FLEX.war file)",
        gitHubCheckoutDir: "setretail10",
        gitHubOwnerAndProject: "crystalservice/setretail10",
        buildType: "ear",
        isToBuildFlex: false,
        clientType: "lenta"
).build(this)

// build_lenta_pull_request_ear_without_flex
new ServerJobTemplateBuilder(
        name: "build_lenta_pull_request_ear_without_flex",
        description: "Building lenta pull request ear server without flex(empty FLEX.war file)",
        gitHubCheckoutDir: "setretail10",
        gitHubOwnerAndProject: "crystalservice/setretail10",
        buildType: "ear",
        isToBuildFlex: false,
        clientType: "lenta",
        isPullRequest: true
).build(this)



