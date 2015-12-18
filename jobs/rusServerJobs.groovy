import com.dslexample.ServerJobTemplateBuilder

/**
 * Russian(default) server build jobs:
 * - with flex
 * -- build_ear_flex
 * -- build_exe_flex
 * -- build_tgz_flex
 * -- build_iso_flex
 * - without flex
 * -- build_ear_without_flex
 */

// build_ear_flex
new ServerJobTemplateBuilder(
        name: "build_ear_flex",
        description: "Building ear server with flex",
        gitHubCheckoutDir: "setretail10",
        gitHubOwnerAndProject: "crystalservice/setretail10",
        buildType: "ear",
        isToBuildFlex: true,
        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
        antSourceDir: "setretail10/SetRetail10_Server_GUI",
        antFlexSdkDir: "/opt/flexsdk",
        antAirSdkDir: "/opt/airsdk"
).build(this)

// build_exe_flex
new ServerJobTemplateBuilder(
        name: "build_exe_flex",
        description: "Building exe server with flex",
        gitHubCheckoutDir: "setretail10",
        gitHubOwnerAndProject: "crystalservice/setretail10",
        buildType: "exe",
        isToBuildFlex: true,
        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
        antSourceDir: "setretail10/SetRetail10_Server_GUI",
        antFlexSdkDir: "/opt/flexsdk",
        antAirSdkDir: "/opt/airsdk"
).build(this)

// build_tgz_flex
new ServerJobTemplateBuilder(
        name: "build_tgz_flex",
        description: "Building tgz server with flex",
        gitHubCheckoutDir: "setretail10",
        gitHubOwnerAndProject: "crystalservice/setretail10",
        buildType: "tgz",
        isToBuildFlex: true,
        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
        antSourceDir: "setretail10/SetRetail10_Server_GUI",
        antFlexSdkDir: "/opt/flexsdk",
        antAirSdkDir: "/opt/airsdk"
).build(this)

// build_iso_flex
new ServerJobTemplateBuilder(
        name: "build_iso_flex",
        description: "Building iso server with flex",
        gitHubCheckoutDir: "setretail10",
        gitHubOwnerAndProject: "crystalservice/setretail10",
        gitHubOwnerAndProjectLinuxSources: "crystalservice/setretail10linux",
        gitHubCheckoutDirLinuxSources: "setretail10linux",
        buildType: "iso",
        isToBuildFlex: true,
        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
        antSourceDir: "setretail10/SetRetail10_Server_GUI",
        antFlexSdkDir: "/opt/flexsdk",
        antAirSdkDir: "/opt/airsdk"

).build(this)

// build_ear_without_flex
new ServerJobTemplateBuilder(
        name: "build_ear_without_flex",
        description: "Building ear server without flex(empty FLEX.war file)",
        gitHubCheckoutDir: "setretail10",
        gitHubOwnerAndProject: "crystalservice/setretail10",
        buildType: "ear",
        isToBuildFlex: false,
).build(this)

