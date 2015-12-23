import com.setdsl.CashJobTemplateBuilder

/**
 * BUILD -> DEPLOY
 */

// build_pos_cash_tar
new CashJobTemplateBuilder(
        name: 'deploy_pos_cash_n_robot',
        description: 'Builds and deploys pos cash with robot',
        buildType: "tar",
        clientType: "pos",
        isToDeployCash: true,
        isToDeployRobot: true
).build(this)

