package com.setdsl

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job


/*
* Костыль, т.к. у нас не собирается flex на windows :)
* */

class ServerExeJobTemplateWrapper {
    String name
    String description

    String gitHubCredentials = "4e269209-1b8f-4f0b-a849-c7376ed088e0"
    String gitHubOwnerAndProject = "crystalservice/setretail10"
    String gitHubCheckoutDir = "setretail10"

    String buildType = "exe"
    String clientType


    String antBuildFile = "setretail10/SetRetail10_Server_GUI/build.xml"
    String antSourceDir = "setretail10/SetRetail10_Server_GUI"

    String antFlexSdkDir = "c:/airsdk"
    String antAirSdkDir = "c:/flexsdk"
    String antBuildTestFile

    Boolean isToPublishUnitTests = true
    Boolean isToPublishJaCoCo = true
    Boolean isToRunProtocolValidation = false

    Boolean isToBuildByPush = false

    String artifacts = 'FLEX.war,*.branch,*.ear,*.sh,*.tgz,*.iso, *.exe'

    List<String> emails = ['v.chernov@crystals.ru']


    Job build(DslFactory dslFactory) {
        dslFactory.multiJob(name) {
            it.description this.description
            logRotator {
                numToKeep 20
            }

            label('master')

            parameters {
                stringParam('VERSION', '10.2.99.0', '')
                stringParam('BRANCH', 'master', 'Server sources; gitHub branch or tag')
                booleanParam('FLEX_DEBUG', false, '')
                booleanParam('FLEX_TEST_MODE', false, '')

            }

            steps {

                phase('Build flex') {
                    phaseJob('build_flex') {
                        currentJobParameters(true)
                        parameters {
                            currentBuild()
                            nodeLabel('master', 'master')
                        }
                    }
                }


                phase('Build and deploy') {
                    phaseJob('build_exe_without_flex') {
                        currentJobParameters(true)
                        parameters {
                            currentBuild()
                            nodeLabel('windows_test','windows_test')
                        }
                    }
                }

            } // steps
        }
    }
}

