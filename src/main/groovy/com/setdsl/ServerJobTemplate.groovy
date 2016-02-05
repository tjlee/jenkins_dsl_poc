package com.setdsl

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

/**
 * Need copy artifacts job
 * Need pull requests handling (without hook)
 */

/**
 * Build Server Job Template
 */

class ServerJobTemplate {
    String name
    String description

//    String gitHubCredentials = "31df12ac-5d1f-495d-99fe-ad351505d316"
    String gitHubCredentials = "4e269209-1b8f-4f0b-a849-c7376ed088e0"
    String gitHubOwnerAndProject = "crystalservice/setretail10"
    String gitHubCheckoutDir = "setretail10"

    String gitHubOwnerAndProjectLinuxSources = "crystalservice/setretail10linux"
    String gitHubCheckoutDirLinuxSources = "setretail10linux"

    String buildType /* iso, tgz, exe, ear, distr*/
    String clientType /*default lenta belarus*//*for default leave empty*/

    Boolean isToBuildFlex = false
    Boolean isPullRequest = false

    String antBuildFile = "setretail10/SetRetail10_Server_GUI/build.xml"
    String antSourceDir = "setretail10/SetRetail10_Server_GUI"

    String antFlexSdkDir = "/opt/flexsdk"
    String antAirSdkDir = "/opt/airsdk"
    String antBuildTestFile

    Boolean isToPublishUnitTests = true
    Boolean isToPublishJaCoCo = true
    Boolean isToRunProtocolValidation = false


    Boolean isToDeploy = false // is it here ? or maybe implement in another and make build flow &*&(**?


    String artifacts = 'FLEX.war,*.branch,*.ear,*.sh,*.tgz,*.iso, *.exe'

    List<String> emails = []

    Job build(DslFactory dslFactory) {
        dslFactory.job(name) {
            it.description this.description
            logRotator {
                numToKeep 50
            }

            if (this.buildType == "exe") {
                label('build_agent1_j7w64||build_agent3_j7w64')
            } else {
                label('master')
            }

            parameters {
                stringParam('VERSION', '10.2.0.0', '')
                stringParam('BRANCH', 'master', '')

                if (this.buildType) {
                    booleanParam('WILD_FLY', false, '')
                }

                if (this.isToBuildFlex) {
                    booleanParam('FLEX_DEBUG', false, '')
                    booleanParam('FLEX_TEST_MODE', false, '')
                }
            }

            multiscm {

                // build branch/tag (default isPullRequest= false)
                if (!this.isPullRequest) {

                    git {
                        remote {
                            github(this.gitHubOwnerAndProject)
                            credentials(this.gitHubCredentials)
                            branch('\$BRANCH')
                            refspec('+refs/heads/*:refs/remotes/origin/*')
                        }

                        cloneTimeout 20
                        relativeTargetDir(this.gitHubCheckoutDir)
//                        wipeOutWorkspace true
                    }
                }
                // build pull request
                else {
                    git {
                        remote {
                            github(this.gitHubOwnerAndProject)
                            credentials(this.gitHubCredentials)
                            branch('remotes/origin/pr/\$BRANCH/merge')
                            refspec('+refs/pull/*:refs/remotes/origin/pr/*')
                        }

                        cloneTimeout 20
                        relativeTargetDir(this.gitHubCheckoutDir)
//                        wipeOutWorkspace true
                    }
                }
                // if iso need to checkout linux repo
                if (this.buildType == "iso") {
                    git {
                        remote {
                            github(this.gitHubOwnerAndProjectLinuxSources)
                            credentials(this.gitHubCredentials)
                            branch('master')
                            refspec('+refs/heads/*:refs/remotes/origin/*')
                        }

                        cloneTimeout 20
                        relativeTargetDir(this.gitHubCheckoutDirLinuxSources)
//                        wipeOutWorkspace true

                    }
                }
            }


            steps {

                if (this.isToBuildFlex) {

                    shell('mkdir -p \$WORKSPACE/\$JOB_NAME;cd \$WORKSPACE/' + this.gitHubCheckoutDir + ';')

                    ant {
                        targets([])
                        buildFile("\$WORKSPACE/" + this.antBuildFile)
                        antInstallation('Default')

                        props('version': "\$VERSION",
                                'base.source.dir': "\$WORKSPACE/" + this.antSourceDir,
                                'base.build.dir': "\$WORKSPACE/\$JOB_NAME",
                                'flex.sdk.dir': this.antFlexSdkDir,
                                'test.mode': "\$FLEX_TEST_MODE",
                                'compile.help.enabled': true,
                                'compile.debug': "\$FLEX_DEBUG",
                                'air.sdk.dir': this.antAirSdkDir,
                                'protocol.validator.skip': !this.isToRunProtocolValidation,
                                'protocol.validator.report.dir': "\$WORKSPACE/protocolValidationReport")
                    }

                    shell('cd \$WORKSPACE/\$JOB_NAME; rm tests -f -r; rm *.cache -f -r; zip -r ../FLEX.war ./;')

                    if (this.antBuildTestFile) {
                        shell('killall Xvnc Xrealvnc || true; rm -fv /tmp/.X*-lock /tmp/.X11-unix/X*')
                        ant {

                            targets([])
                            buildFile("\$WORKSPACE/" + this.antBuildTestFile)
                            antInstallation('Default')
                            props(
                                    'version': "\$VERSION",
                                    'base.source.dir': "\$WORKSPACE/" + this.antSourceDir,
                                    'base.build.dir': "\$WORKSPACE/\$JOB_NAME",
                                    'flex.sdk.dir': this.antFlexSdkDir,
                                    'test.mode': "\$FLEX_TEST_MODE",
                                    'compile.help.enabled': true,
                                    'compile.debug': "\$FLEX_DEBUG",
                                    'air.sdk.dir': this.antAirSdkDir,
                                    'tests.headless': true)

                        }
                        shell('killall Xvnc Xrealvnc || true;')
                    }


                    shell('cp -f "\$WORKSPACE/FLEX.war" "\$WORKSPACE/' + this.gitHubCheckoutDir + '/SetRetail10_Server/buildGradle/"')

                } else {

                    shell('cd "\$WORKSPACE/' + this.gitHubCheckoutDir + '/SetRetail10_Server/buildGradle"; touch FLEX.war;')
                }


                if (this.buildType) {
                    // copy gradle wrapper
                    shell('''
                        mkdir -p "\$WORKSPACE/gradle/wrapper";
                        cp /opt/gradle/wrapper/* \$WORKSPACE/gradle/wrapper || true;
                        cp /opt/gradlew \$WORKSPACE/gradlew || true;
                    ''')

                    // building iso, tgz, ear, exe, distr
                    if (this.buildType == "tgz" || this.buildType == "iso") {

                        // TODO:
                        // TODO: remove -xtest and change for -Ptest
                        gradle('clean makeTar',
                                '-PtempDir=/tmp -PmoduleVersion="\$VERSION" -PdistrDir="\$WORKSPACE" -Pbranch="\$GIT_BRANCH" -Pshaid="\$GIT_COMMIT" -PuseEmu -Plinux' +
                                        (this.clientType ? ' -PclientId=' + this.clientType : ''),
                                true) {
                            it / rootBuildScriptDir('\$WORKSPACE/' + this.gitHubCheckoutDir + '/SetRetail10_Server/Installation')
                            it / wrapperScript('gradlew')
                            it / makeExecutable(true)
                            it / fromRootBuildScriptDir(false)

                        }

                        // copy to workspace
                        shell('mv -f "\$WORKSPACE/' + this.gitHubCheckoutDir + '/SetRetail10_Server/Installation/build/Set\$VERSION' +
                                (this.clientType ? '-\$CLIENT_TYPE' : '') + '.tgz" "\$WORKSPACE/"')

                    } else if (this.buildType == "distr") {
                        // for patch build

                        gradle('clean makeDistr',
                                '-PtempDir=/tmp -PmoduleVersion="\$VERSION" -PdistrDir="\$WORKSPACE" -Pbranch="\$GIT_BRANCH" -Pshaid="\$GIT_COMMIT" -PuseEmu -Plinux' +
                                        (this.clientType ? ' -PclientId=' + this.clientType : ''),
                                true) {
                            it / rootBuildScriptDir('\$WORKSPACE/' + this.gitHubCheckoutDir + '/SetRetail10_Server/Installation')
                            it / wrapperScript('gradlew')
                            it / makeExecutable(true)
                            it / fromRootBuildScriptDir(false)

                        }
                        // todo: figure out where to move build artifacts!
                        // /bin/cp -f -r $WORKSPACE_DIR/SetRetail10_Server/Installation/build/Set10 $COPY_TO


                    } else if (this.buildType == "ear") {
                        gradle('clean ear test',
                                '-PtempDir=/tmp -PmoduleVersion="\$VERSION" -PdistrDir="\$WORKSPACE" -Pbranch="\$GIT_BRANCH" -Pshaid="\$GIT_COMMIT" -PuseEmu -PwildFly="\$WILD_FLY"' +
                                        (this.clientType ? ' -PclientId=' + this.clientType : ''),
                                true) {
                            it / rootBuildScriptDir('\$WORKSPACE/' + this.gitHubCheckoutDir + '/SetRetail10_Server/buildGradle')
                            it / wrapperScript('gradlew')
                            it / makeExecutable(true)
                            it / fromRootBuildScriptDir(false)
                        }

                    } else if (this.buildType == "exe") {

                        gradle('makeWinInstaller',
                                '-PtempDir=/c/Temp -PmoduleVersion=\$VERSION -PdistrDir=\$WORKSPACE -Pbranch=\$BRANCH -Pshaid=\$GIT_COMMIT -PuseEmu -Ptest -PwildFly=\$WILD_FLY' +
                                        (this.clientType ? ' -PclientId=' + this.clientType : ''),
                                true) {
                            it / rootBuildScriptDir('\$WORKSPACE/' + this.gitHubCheckoutDir + '/SetRetail10_Server/Installation')
                            it / wrapperScript('gradlew')
                            it / makeExecutable(true)
                            it / fromRootBuildScriptDir(false)
                        }

                        // copying with bash.exe on windows
                        shell('''
                    cd "\$WORKSPACE";
                    for file in *.exe
                    do
                    if [[ "$file" == *"setup.exe"* ]]
                    then
                    mv -fv "$file" Set"\$VERSION"''' + (this.clientType ? '_' + this.clientType : '') + '''_setup.exe
                    fi
                    done''')
                    }

                    shell('mv -f "\$WORKSPACE/' + this.gitHubCheckoutDir + '/SetRetail10_Server/buildGradle/build/libs/Set10.ear" "\$WORKSPACE"')
                    shell('cd "\$WORKSPACE/' + this.gitHubCheckoutDir + '/SetRetail10_Server/buildGradle"; rm ./*.war -f;')

                    // pack up iso
                    if (this.buildType == "iso") {

                        shell('cd "\$WORKSPACE/' + this.gitHubCheckoutDirLinuxSources + '/server/"')
                        shell('\$WORKSPACE/' + this.gitHubCheckoutDirLinuxSources + '/server/build7.sh -d="\$WORKSPACE" -s="\$WORKSPACE/Set\$VERSION' +
                                (this.clientType ? '-\$CLIENT_TYPE' : '') + '.tgz"')

                    }


                }
            }

            // todo: copy to aftifacts server

            publishers {

                if (this.isToPublishUnitTests) {
                    archiveJunit('**/test-results/*.xml')
                }

                if (this.isToPublishJaCoCo) {
                    jacocoCodeCoverage {
                        exclusionPattern('**/*ContextFactory$1*, **/*ContextFactory*,*/Aladdin/**,**/hasp/**,*/org/**, **/vo/**,**/xmlview/**,**/xmlstubs/**,**/jestery/**,**/supermag/**,**/xml/**,**/enums/**,**/visualization/**,**/exceptions/**,**/exception/**,**/stubs/**,**/newstub/**,**/test/**')
                    }
                }
                // todo remove from job parametes
                if (this.isToRunProtocolValidation) {
                    publishHtml {
                        report('\$WORKSPACE/protocolValidationReport') {
                            allowMissing(true)
                            reportName('Protocol validation report')
                        }

                    }
                }

                if (this.emails) {
                    mailer this.emails.join(' ')
                }

                archiveArtifacts {
                    pattern(this.artifacts)
                    onlyIfSuccessful()
                }

            }

        }


    }
}
































