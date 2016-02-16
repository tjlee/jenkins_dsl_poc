import com.setdsl.CashJobTemplate
import com.setdsl._DeployLinux
import com.setdsl.DeployLinuxServerJobTemplate

/**
 * BUILD -> DEPLOY
 */

// build_pos_cash_tar
new CashJobTemplate(
        name: 'deploy_pos_cash_n_robot',
        description: 'Builds and deploys pos cash with robot',
        buildType: "tar",
        clientType: "pos",
        isToDeployCash: true,
        isToDeployRobot: true
).build(this)

// deploy_pull_request_pos_cash_n_robot
new CashJobTemplate(
        name: 'deploy_pull_request_pos_cash_n_robot',
        description: 'Builds and deploys pos cash with robot',
        buildType: "tar",
        clientType: "pos",
        isToDeployCash: true,
        isToDeployRobot: true,
        isPullRequest: true
).build(this)

// build_pos_cash_tar
new CashJobTemplate(
        name: 'deploy_pos_cash',
        description: 'Builds and deploys pos cash',
        buildType: "tar",
        clientType: "pos",
        isToDeployCash: true
).build(this)


new _DeployLinux(
        name:'deploy_linux',
        description: 'private job deploy linux',
).build(this)

new DeployLinuxServerJobTemplate(
        name:'deploy_linux_with_building',
        description: 'Deploys centrum and retail on linux servers',
).build(this)