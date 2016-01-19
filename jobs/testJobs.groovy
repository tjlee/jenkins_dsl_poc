import com.setdsl.RestoreVirtualPcStateJobTemplate
import com.setdsl._RunTests
import com.setdsl.RunTestsMultiJobTemplate

new _RunTests(
        name: "rus_test_run_without_deployment",
        description: 'Cashes are always deploying',
        isToConfig: true,
        isToRunRobot: true
).build(this)


new RunTestsMultiJobTemplate(
        name: 'rus_test_run_wrapped_unix',
        description: ''
).build(this)

new RestoreVirtualPcStateJobTemplate(
        name: 'restore_virtual_pc_state',
        description: ''
).build(this)