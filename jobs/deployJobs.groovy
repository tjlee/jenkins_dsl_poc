import com.setdsl.CashJobTemplateBuilder
import com.setdsl.DeployLinux
import com.setdsl.DeployLinuxServerTemplateBuilder

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

new DeployLinux(
        name:'deploy_linux',
        description: '',
).build(this)

new DeployLinuxServerTemplateBuilder(
        name:'deploy_linux_with_building',
        description: '',
).build(this)