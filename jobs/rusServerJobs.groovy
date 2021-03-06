import com.setdsl.ServerExeJobTemplate
import com.setdsl.ServerExeJobTemplateWrapper
import com.setdsl.ServerJobTemplate

/**
 * Russian(default) server build jobs:
 * - with flex
 * -- build_ear_flex
 * -- build_exe_flex
 * -- build_tgz_flex
 * -- build_sh_flex
 * -- build_iso_flex
 * -- build_distr_flex
 * - without flex
 * -- build_ear_without_flex
 * -- build_distr_without_flex
 * -- build_sh_without_flex
 * -- todo: remove me -- build_sh_without_flex
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


new ServerExeJobTemplateWrapper(
        name: "build_exe_with_flex",
        description: "Builds exe with flex",

).build(this)


new ServerExeJobTemplate(
        name: "build_exe_without_flex",
        description: "Building exe server without flex"
).build(this)


// build_exe_flex
//new ServerJobTemplate(
//        name: "build_exe_flex",
//        description: "Building exe server with flex",
//        buildType: "exe",
//        isToBuildFlex: true
//).build(this)

//new ServerJobTemplate(
//        name: "build_exe_without_flex",
//        description: "Building exe server without flex",
//        buildType: "exe",
//        isToBuildFlex: false
//).build(this)

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
        isToPublishUnitTests: false,
        isCustomWorkspace: true
).build(this)

//build_sh_flex
new ServerJobTemplate(
        name: "build_sh_flex",
        description: "Building sh with flex",
        buildType: "sh",
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
        isToBuildFlex: false,
        isCustomWorkspace: true
).build(this)

//build_sh_without_flex
new ServerJobTemplate(
        name: "build_sh_without_flex",
        description: "Building sh without Flex",
        buildType: "sh",
        isToBuildFlex: false,
        isToPublishUnitTests: false
).build(this)


// build_pull_request_exe_flex
//new ServerJobTemplate(
//        name: "build_pull_request_exe_flex",
//        description: "Building pull request exe server with flex",
//        buildType: "exe",
//        isToBuildFlex: true,
//        isPullRequest: true
//).build(this)

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

// build_master_ear_without_flex_after_push_to_repo
new ServerJobTemplate(
        name: "build_master_ear_without_flex_after_push_to_repo",
        description: "Builds master after each push to master repo",
        buildType: "ear",
        isToBuildFlex: false,
        isToBuildByPush: true
)
