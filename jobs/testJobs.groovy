import com.setdsl.RestoreVirtualPcStateJobTemplate
import com.setdsl._RunTests
import com.setdsl.RunTestsMultiJobTemplate

new _RunTests(
        name: "rus_test_run_without_deployment",
        description: 'Cashes are always deploying, servers are not'
//        isToConfig: true,
//        isToRunRobot: true
).build(this)


new RunTestsMultiJobTemplate(
        name: 'run_overall_build_deploy_on_linux',
        description: 'Builds flex, sh, cashes, redeploys stand c and starts test run(all suites)'
//        isToRunTests: false

).build(this)

new RestoreVirtualPcStateJobTemplate(
        name: 'restore_virtual_pc_state',
        description: ''
).build(this)