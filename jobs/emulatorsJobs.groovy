import com.setdsl.SapEmulatorJobTemplateBuilder

new SapEmulatorJobTemplateBuilder(
        name: 'emulators_build_sap',
        description: "Builds SAP emulator"
).build(this)

new SapEmulatorJobTemplateBuilder(
        name: 'emulators_pull_request_build_sap',
        description: "Builds SAP emulator",
        isPullRequest: true
).build(this)