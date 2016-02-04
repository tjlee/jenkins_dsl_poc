//import com.setdsl.ServerJobTemplate
//
///**
// * Lenta server build jobs:
// * - with flex
// * -- build_lenta_ear_flex
// * -- build_lenta_exe_flex
// * -- build_lenta_tgz_flex
// * -- build_lenta_iso_flex
// * - without flex
// * -- build_lenta_ear_without_flex
// *
// * - pull requests
// * -- build_lenta_pull_request_ear_without_flex
// */
//
//// build_lenta_ear_flex
//new ServerJobTemplate(
//        name: "build_lenta_ear_flex",
//        description: "Building lenta ear server with flex",
//        buildType: "ear",
//        isToBuildFlex: true,
//        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
//        antSourceDir: "setretail10/SetRetail10_Server_GUI",
//        clientType: "lenta"
//).build(this)
//
//// build_lenta_exe_flex
//new ServerJobTemplate(
//        name: "build_lenta_exe_flex",
//        description: "Building lenta exe server with flex",
//        buildType: "exe",
//        isToBuildFlex: true,
//        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
//        antSourceDir: "setretail10/SetRetail10_Server_GUI",
//        clientType: "lenta"
//).build(this)
//
//// build_lenta_tgz_flex
//new ServerJobTemplate(
//        name: "build_lenta_tgz_flex",
//        description: "Building lenta tgz server with flex",
//        buildType: "tgz",
//        isToBuildFlex: true,
//        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
//        antSourceDir: "setretail10/SetRetail10_Server_GUI",
//        clientType: "lenta"
//).build(this)
//
//// build_lenta_iso_flex
//new ServerJobTemplate(
//        name: "build_lenta_iso_flex",
//        description: "Building lenta iso server with flex",
//        buildType: "iso",
//        isToBuildFlex: true,
//        antBuildFile: "setretail10/SetRetail10_Server_GUI/build.xml",
//        antSourceDir: "setretail10/SetRetail10_Server_GUI",
//        clientType: "lenta"
//
//).build(this)
//
//// build_lenta_ear_without_flex
//new ServerJobTemplate(
//        name: "build_lenta_ear_without_flex",
//        description: "Building lenta ear server without flex(empty FLEX.war file)",
//        buildType: "ear",
//        isToBuildFlex: false,
//        clientType: "lenta"
//).build(this)
//
//// build_lenta_pull_request_ear_without_flex
//new ServerJobTemplate(
//        name: "build_lenta_pull_request_ear_without_flex",
//        description: "Building lenta pull request ear server without flex(empty FLEX.war file)",
//        buildType: "ear",
//        isToBuildFlex: false,
//        clientType: "lenta",
//        isPullRequest: true
//).build(this)
//
//
//
