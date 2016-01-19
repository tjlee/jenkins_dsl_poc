import com.setdsl.ManageVirtualPcJobTemplateBuilder
import com.setdsl.RunTestsTemplateBuilder
import com.setdsl.RunTestsWrapperBuilder

new RunTestsTemplateBuilder(
        name: "rus_test_run_without_deployment",
        description: 'Cashes are always deploying',
        isToConfig: true,
        isToRunRobot: true
).build(this)


new RunTestsWrapperBuilder(
        name: 'rus_test_run_wrapped_unix',
        description: ''
).build(this)

new ManageVirtualPcJobTemplateBuilder(
        name: 'restore_virtual_pc_state',
        description: ''
).build(this)