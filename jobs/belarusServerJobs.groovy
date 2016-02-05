//import com.setdsl.ServerJobTemplate
//
///**
// * Belarus server build jobs:
// * - with flex
// * -- build_belarus_ear_flex
// * -- build_belarus_exe_flex
// * -- build_belarus_tgz_flex
// * -- build_belarus_iso_flex
// * - without flex
// * -- build_belarus_ear_without_flex
// *
// * - pull requests
// * -- build_belarus_pull_request_ear_without_flex
// */
//
//// build_belarus_ear_flex
//new ServerJobTemplate(
//        name: "build_belarus_ear_flex",
//        description: "Building belarus ear server with flex",
//        buildType: "ear",
//        isToBuildFlex: true,
//        clientType: "belarus"
//).build(this)
//
//// build_belarus_exe_flex
//new ServerJobTemplate(
//        name: "build_belarus_exe_flex",
//        description: "Building belarus exe server with flex",
//        buildType: "exe",
//        isToBuildFlex: true,
//        clientType: "belarus"
//).build(this)
//
//// build_belarus_tgz_flex
//new ServerJobTemplate(
//        name: "build_belarus_tgz_flex",
//        description: "Building belarus tgz server with flex",
//        buildType: "tgz",
//        isToBuildFlex: true,
//        clientType: "belarus"
//).build(this)
//
//// build_belarus_iso_flex
//new ServerJobTemplate(
//        name: "build_belarus_iso_flex",
//        description: "Building belarus iso server with flex",
//        buildType: "iso",
//        isToBuildFlex: true,
//        clientType: "belarus"
//
//).build(this)
//
//// build_belarus_ear_without_flex
//new ServerJobTemplate(
//        name: "build_belarus_ear_without_flex",
//        description: "Building belarus ear server without flex(empty FLEX.war file)",
//        buildType: "ear",
//        isToBuildFlex: false,
//        clientType: "belarus"
//).build(this)
//
//// build_belarus_pull_request_ear_without_flex
//new ServerJobTemplate(
//        name: "build_belarus_pull_request_ear_without_flex",
//        description: "Building belarus pull request ear server without flex(empty FLEX.war file)",
//        buildType: "ear",
//        isToBuildFlex: false,
//        clientType: "belarus",
//        isPullRequest: true
//).build(this)
//
