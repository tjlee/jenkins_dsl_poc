import com.setdsl.CashJobTemplateBuilder

/**
 * Lenta cash build jobs:
 * - build_lenta_pos_cash_tar
 * - build_lenta_pos_cash_iso
 *
 * - build_lenta_pull_request_pos_cash_tar
 * - build_lenta_pull_request_pos_cash_iso
 */

// build_lenta_pos_cash_tar
new CashJobTemplateBuilder(
        name: 'build_lenta_pos_cash_tar',
        description: 'Builds lenta pos cash tar',
        buildType: "tar",
        clientType: "lenta"
).build(this)

// build_lenta_pos_cash_iso
new CashJobTemplateBuilder(
        name: 'build_lenta_pos_cash_iso',
        description: 'Builds lenta pos cash iso',
        buildType: "iso",
        clientType: "lenta"
).build(this)

// build_pull_request_pos_cash_tar
new CashJobTemplateBuilder(
        name: 'build_lenta_pull_request_pos_cash_tar',
        description: 'Builds lenta pull request pos cash tar',
        buildType: "tar",
        clientType: "lenta",
        isPullRequest: true
).build(this)

// build_lenta_pull_request_pos_cash_iso
new CashJobTemplateBuilder(
        name: 'build_lenta_pull_request_pos_cash_iso',
        description: 'Builds lenta pull request pos cash iso',
        buildType: "iso",
        clientType: "lenta",
        isPullRequest: true
).build(this)


