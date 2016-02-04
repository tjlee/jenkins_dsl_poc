import com.setdsl.ServerJobTemplate

/**
 * Russian(default) server build jobs:
 * - with flex
 * -- build_ear_flex
 * -- build_exe_flex
 * -- build_tgz_flex
 * -- build_iso_flex
 * - without flex
 * -- build_ear_without_flex
 *
 * - pull requests
 * -- build_pull_request_exe_flex
 * -- build_pull_request_iso_flex
 * -- build_pull_request_ear_without_flex
 */

// build_ear_flex
new ServerJobTemplate(
        name: "build_ear_flex",
        description: "Building ear server with flex",
        buildType: "ear",
        isToBuildFlex: true,
        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
        antSourceDir: "setretail10/SetRetail10_Server_GUI",
).build(this)

// build_exe_flex
new ServerJobTemplate(
        name: "build_exe_flex",
        description: "Building exe server with flex",
        buildType: "exe",
        isToBuildFlex: true,
        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
        antSourceDir: "setretail10/SetRetail10_Server_GUI"
).build(this)

// build_tgz_flex
new ServerJobTemplate(
        name: "build_tgz_flex",
        description: "Building tgz server with flex",
        buildType: "tgz",
        isToBuildFlex: true,
        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
        antSourceDir: "setretail10/SetRetail10_Server_GUI",
        isToPublishUnitTests: false // TODO: remove me
).build(this)

// build_iso_flex
new ServerJobTemplate(
        name: "build_iso_flex",
        description: "Building iso server with flex",
        buildType: "iso",
        isToBuildFlex: true,
        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
        antSourceDir: "setretail10/SetRetail10_Server_GUI",

).build(this)

// build_ear_without_flex
new ServerJobTemplate(
        name: "build_ear_without_flex",
        description: "Building ear server without flex(empty FLEX.war file)",
        buildType: "ear",
        isToBuildFlex: false,
).build(this)

// build_pull_request_exe_flex
new ServerJobTemplate(
        name: "build_pull_request_exe_flex",
        description: "Building pull request exe server with flex",
        buildType: "exe",
        isToBuildFlex: true,
        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
        antSourceDir: "setretail10/SetRetail10_Server_GUI",
        isPullRequest: true
).build(this)

// build_pull_request_iso_flex
new ServerJobTemplate(
        name: "build_pull_request_iso_flex",
        description: "Building pull request iso server with flex",
        buildType: "iso",
        isToBuildFlex: true,
        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
        antSourceDir: "setretail10/SetRetail10_Server_GUI",
        isPullRequest: true
).build(this)

// build_pull_request_ear_without_flex
new ServerJobTemplate(
        name: "build_pull_request_ear_without_flex",
        description: "Building ear pull request server without flex(empty FLEX.war file)",
        buildType: "ear",
        isToBuildFlex: false,
        isPullRequest: true
).build(this)