package com.setdsl

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

/**
 * Need copy artifacts job
 * Need pull requests handling (without hook)
 * Need web hook for pull requests
 */

/**
 * Build Server Job Template
 */

class ServerJobTemplateBuilder {
    String name
    String description

    String gitHubCredentials = "31df12ac-5d1f-495d-99fe-ad351505d316"

    String gitHubOwnerAndProject
    String gitHubCheckoutDir


    String gitHubOwnerAndProjectLinuxSources
    String gitHubCheckoutDirLinuxSources


    String buildType
    String clientType /*default lenta belarus*//*for default leave empty*/

    Boolean isToBuildFlex = false

    String antBuildFile
    String antSourceDir
    String antFlexSdkDir
    String antAirSdkDir
    String antBuildTestFile

    Boolean isToPublishUnitTests = true
    Boolean isToPublishJaCoCo = true
    Boolean isToRunProtocolValidation = false

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
//                    booleanParam('JACOCO', true, '')
//                    booleanParam('JUNIT', true, '')
                    booleanParam('WILD_FLY', false, '')
                }

                if (this.isToBuildFlex) {
                    booleanParam('FLEX_DEBUG', false, '')
//                    booleanParam('PROTOCOL_VALIDATION_SKIP', true, '')
                    booleanParam('FLEX_TEST_MODE', false, '')
                }
            }

            multiscm {

                    git {
                        remote {
                            github(this.gitHubOwnerAndProject)
                            credentials(this.gitHubCredentials)

                            branch('\$BRANCH')
                            refspec('+refs/*:refs/remotes/origin/*')
//                            +refs/pull/*:refs/remotes/origin/pr/*')
//                            refspec('+refs/pull/*:refs/remotes/origin/pr/*')


                            // refspec +/refs/



                            /**

                             branch('\$BRANCH')
                             refspec('+refs/heads/*:refs/remotes/origin/*')


                             BRANCH_NAME=remotes/origin/pr/$BRANCH/merge
                             BUILD_NAME=pull_$BRANCH
                             refspec('+refs/pull/*:refs/remotes/origin/pr/*')

                             */

                        }
                        cloneTimeout 20
                        relativeTargetDir(this.gitHubCheckoutDir)
                        wipeOutWorkspace true
                    }


//                    git {
//                        remote {
//                            github(this.gitHubOwnerAndProject)
//                            credentials(this.gitHubCredentials)
//
//                            branch('remotes/origin/pr/\$PULL_NUMBER/merge')
//                            refspec('+refs/pull/*:refs/remotes/origin/pr/*')
//
//                            /**
//
//                             BRANCH_NAME=remotes/origin/pr/$BRANCH/merge
//                             BUILD_NAME=pull_$BRANCH
//                             refspec('+refs/pull/*:refs/remotes/origin/pr/*')
//
//                             */
//
//                        }
//                        cloneTimeout 20
//                        relativeTargetDir(this.gitHubCheckoutDir)
//                        wipeOutWorkspace true
//                    }


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
                        wipeOutWorkspace true

                    }
                }


            }


            steps {

                if (this.isToBuildFlex) {

                    shell('makedir -p \$WORKSPACE/\$JOB_NAME;cd \$WORKSPACE/' + this.gitHubCheckoutDir + ';')
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
                    shell('cd "\$WORKSPACE/' + this.gitHubCheckoutDir + '/SetRetail10_Server/Installation"')

                    // building iso, tgz, ear, exe
                    if (this.buildType == "tgz" || this.buildType == "iso") {

                        gradle('clean makeTar',
                                '-PtempDir=/tmp -PmoduleVersion="\$VERSION" -PdistrDir="\$WORKSPACE" -Pbranch="\$GIT_BRANCH" -Pshaid="\$GIT_COMMIT" -PuseEmu -Ptest -Plinux' +
                                        (this.clientType ? ' -PclientId=' + this.clientType : ''),
                                true) {
                            it / wrapperScript('gradlew')
                        }

                        // copy to workspace
                        shell('mv -f "\$WORKSPACE/' + this.gitHubCheckoutDir + '/SetRetail10_Server/Installation/build/Set\$VERSION' +
                                (this.clientType ? '-\$CLIENT_TYPE' : '') + '.tgz" "\$WORKSPACE/"')

                    } else if (this.buildType == "ear") {

                        shell('cd "\$WORKSPACE/' + this.gitHubCheckoutDir + '/SetRetail10_Server/buildGradle"')

                        gradle('clean ear test',
                                '-PtempDir=/tmp -PmoduleVersion="\$VERSION" -PdistrDir="\$WORKSPACE" -Pbranch="\$GIT_BRANCH" -Pshaid="\$GIT_COMMIT" -PuseEmu -PwildFly="\$WILD_FLY"' +
                                        (this.clientType ? ' -PclientId=' + this.clientType : ''),
                                true) {
                            it / wrapperScript('gradlew')
                        }

                    } else if (this.buildType == "exe") {
                        // todo: !!!


                        gradle('makeWinInstaller',
                                '-PtempDir=/c/Temp -PmoduleVersion=\$VERSION -PdistrDir=\$WORKSPACE -Pbranch=\$BRANCH -Pshaid=\$GIT_COMMIT -PuseEmu -Ptest -PwildFly=\$WILD_FLY' +
                                        (this.clientType ? ' -PclientId=' + this.clientType : ''),
                                true) {
                            it / wrapperScript('gradlew')
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
                        shell('./build7.sh -d="\$WORKSPACE" -s="\$WORKSPACE/Set\$VERSION' +
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
































