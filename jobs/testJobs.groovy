import com.setdsl.RunTestsTemplateBuilder
import com.setdsl.RunTestsWrapperBuilder

new RunTestsTemplateBuilder(
        name: "rus_test_run_without_deployment",
        description: '',
        isToConfig: true,
        isToRunRobot: true
).build(this)


new RunTestsWrapperBuilder(
        name: 'rus_test_run_wrapped',
        description: ''
).build(this)