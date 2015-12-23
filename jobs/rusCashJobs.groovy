import com.setdsl.CashJobTemplateBuilder

/**
 * Russian(pos) cash build jobs:
 * - build_pos_cash_tar
 * - build_pos_cash_iso
 *
 * - build_pull_request_pos_cash_tar
 * - build_pull_request_pos_cash_iso
 */

// build_pos_cash_tar
new CashJobTemplateBuilder(
        name: 'build_pos_cash_tar',
        description: 'Builds pos cash tar',
        buildType: "tar",
        clientType: "pos"
).build(this)

// build_pos_cash_iso
new CashJobTemplateBuilder(
        name: 'build_pos_cash_iso',
        description: 'Builds pos cash iso',
        buildType: "iso",
        clientType: "pos"
).build(this)

// build_pull_request_pos_cash_tar
new CashJobTemplateBuilder(
        name: 'build_pull_request_pos_cash_tar',
        description: 'Builds pull request pos cash tar',
        buildType: "tar",
        clientType: "pos",
        isPullRequest: true
).build(this)

// build_pull_request_pos_cash_iso
new CashJobTemplateBuilder(
        name: 'build_pull_request_pos_cash_iso',
        description: 'Builds pull request pos cash iso',
        buildType: "iso",
        clientType: "pos",
        isPullRequest: true
).build(this)
