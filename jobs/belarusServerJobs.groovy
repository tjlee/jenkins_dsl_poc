import com.dslexample.ServerJobTemplateBuilder

/**
 * Belarus server build jobs:
 * - with flex
 * -- build_belarus_ear_flex
 * -- build_belarus_exe_flex
 * -- build_belarus_tgz_flex
 * -- build_belarus_iso_flex
 * - without flex
 * -- build_belarus_ear_without_flex
 */

// build_belarus_ear_flex
new ServerJobTemplateBuilder(
        name: "build_belarus_ear_flex",
        description: "Building lenta ear server with flex",
        gitHubCheckoutDir: "setretail10",
        gitHubOwnerAndProject: "crystalservice/setretail10",
        buildType: "ear",
        isToBuildFlex: true,
        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
        antSourceDir: "setretail10/SetRetail10_Server_GUI",
        antFlexSdkDir: "/opt/flexsdk",
        antAirSdkDir: "/opt/airsdk",
        clientType: "belarus"
).build(this)

// build_belarus_exe_flex
new ServerJobTemplateBuilder(
        name: "build_belarus_exe_flex",
        description: "Building lenta exe server with flex",
        gitHubCheckoutDir: "setretail10",
        gitHubOwnerAndProject: "crystalservice/setretail10",
        buildType: "exe",
        isToBuildFlex: true,
        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
        antSourceDir: "setretail10/SetRetail10_Server_GUI",
        antFlexSdkDir: "/opt/flexsdk",
        antAirSdkDir: "/opt/airsdk",
        clientType: "belarus"
).build(this)

// build_belarus_tgz_flex
new ServerJobTemplateBuilder(
        name: "build_belarus_tgz_flex",
        description: "Building lenta tgz server with flex",
        gitHubCheckoutDir: "setretail10",
        gitHubOwnerAndProject: "crystalservice/setretail10",
        buildType: "tgz",
        isToBuildFlex: true,
        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
        antSourceDir: "setretail10/SetRetail10_Server_GUI",
        antFlexSdkDir: "/opt/flexsdk",
        antAirSdkDir: "/opt/airsdk",
        clientType: "belarus"
).build(this)

// build_belarus_iso_flex
new ServerJobTemplateBuilder(
        name: "build_belarus_iso_flex",
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
        clientType: "belarus"

).build(this)

// build_belarus_ear_without_flex
new ServerJobTemplateBuilder(
        name: "build_belarus_ear_without_flex",
        description: "Building lenta ear server without flex(empty FLEX.war file)",
        gitHubCheckoutDir: "setretail10",
        gitHubOwnerAndProject: "crystalservice/setretail10",
        buildType: "ear",
        isToBuildFlex: false,
        clientType: "belarus"
).build(this)



