import com.setdsl.ServerJobTemplate

/**
 * Russian(default) server build jobs:
 * - with flex
 * -- build_ear_flex
 * -- build_exe_flex
 * -- build_tgz_flex
 * -- build_iso_flex
 * -- todo: build_distr_flex
 * - without flex
 * -- build_ear_without_flex
 * -- todo: build_distr_without_flex
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
        isToBuildFlex: true
).build(this)

// build_exe_flex
new ServerJobTemplate(
        name: "build_exe_flex",
        description: "Building exe server with flex",
        buildType: "exe",
        isToBuildFlex: true
).build(this)

// build_tgz_flex
new ServerJobTemplate(
        name: "build_tgz_flex",
        description: "Building tgz server with flex",
        buildType: "tgz",
        isToBuildFlex: true
//        ,
//
//        isToPublishUnitTests: false // TODO: remove me
).build(this)

// build_iso_flex
new ServerJobTemplate(
        name: "build_iso_flex",
        description: "Building iso server with flex",
        buildType: "iso",
        isToBuildFlex: true

).build(this)

// build_distr_flex
new ServerJobTemplate(
        name: "build_distr_flex",
        description: "Building makeDistr for further patch building",
        buildType: "distr",
        isToBuildFlex: true,
        isToPublishUnitTests: false
).build(this)

// build_ear_without_flex
new ServerJobTemplate(
        name: "build_ear_without_flex",
        description: "Building ear server without flex(empty FLEX.war file)",
        buildType: "ear",
        isToBuildFlex: false
).build(this)

// build_distr_without_flex
new ServerJobTemplate(
        name: "build_distr_without_flex",
        description: "Building makeDistr without Flex for further patch building",
        buildType: "distr",
        isToBuildFlex: false
).build(this)

// build_pull_request_exe_flex
new ServerJobTemplate(
        name: "build_pull_request_exe_flex",
        description: "Building pull request exe server with flex",
        buildType: "exe",
        isToBuildFlex: true,
        isPullRequest: true
).build(this)

// build_pull_request_iso_flex
new ServerJobTemplate(
        name: "build_pull_request_iso_flex",
        description: "Building pull request iso server with flex",
        buildType: "iso",
        isToBuildFlex: true,
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