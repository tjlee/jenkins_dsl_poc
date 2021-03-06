import com.setdsl.CashJobTemplate

/**
 * Russian(pos) cash build jobs:
 * - build_pos_cash_tar
 * - build_pos_cash_iso
 *
 * - build_pull_request_pos_cash_tar
 * - build_pull_request_pos_cash_iso
 */

// build_pos_cash_tar_custom_workspace
new CashJobTemplate(
        name: 'build_pos_cash_tar_custom_workspace',
        description: 'Builds pos cash tar',
        buildType: "tar",
//        clientType: "pos",
        isCustomWorkspace: true
).build(this)

// build_pos_cash_tar
//new CashJobTemplate(
//        name: 'build_pos_cash_tar_custom',
//        description: 'Builds pos cash tar',
//        buildType: "tar",
//        clientType: "pos"
//).build(this)


// build_pos_cash_iso
new CashJobTemplate(
        name: 'build_pos_cash_iso',
        description: 'Builds pos cash iso',
        buildType: "iso"
//        ,
//        clientType: "pos"
).build(this)

// build_pull_request_pos_cash_tar
new CashJobTemplate(
        name: 'build_pull_request_pos_cash_tar',
        description: 'Builds pull request pos cash tar',
        buildType: "tar",
//        clientType: "pos",
        isPullRequest: true
).build(this)

// build_pull_request_pos_cash_iso
//new CashJobTemplate(
//        name: 'build_pull_request_pos_cash_iso',
//        description: 'Builds pull request pos cash iso',
//        buildType: "iso",
//        clientType: "pos",
//        isPullRequest: true
//).build(this)

// build_master_tar_after_push_to_repo
new CashJobTemplate(
        name: 'build_master_tar_after_push_to_repo',
        description: 'Builds pos cash tar after push to repository',
        buildType: "tar",
//        clientType: "pos",
        isToBuildByPush: true
).build(this)
