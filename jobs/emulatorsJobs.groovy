import com.setdsl.SapEmulatorJobTemplate

new SapEmulatorJobTemplate(
        name: 'emulators_build_sap',
        description: "Builds SAP emulator"
).build(this)

new SapEmulatorJobTemplate(
        name: 'emulators_pull_request_build_sap',
        description: "Builds SAP emulator",
        isPullRequest: true
).build(this)