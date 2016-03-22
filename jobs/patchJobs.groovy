import com.setdsl.CashPatchBuilderMultiJobTemplate
import com.setdsl.ServerPatchBuilderMultiJobTemplate

//
//new ServerPatchBuilderMultiJobTemplate(
//        name: "build_server_patch",
//        description: "Building server patch",
//        isToRunTests: false
//).build(this)

new CashPatchBuilderMultiJobTemplate(
        name: "build_cash_patch",
        description: "Building cash patch",
        isToRunTests: false
).build(this)