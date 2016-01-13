package com.setdsl

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job


class SapEmulatorJobTemplateBuilder {
    String name
    String description

    String gitHubCredentials = "31df12ac-5d1f-495d-99fe-ad351505d316" // todo: move to base template job

    String gitHubOwnerAndProject = "crystalservice/setretail10"
    String gitHubCheckoutDir = "setretail10"
    String artifacts = "setretail10/SetRetail10_Utils/testStand/SapWSEmulator/build/libs/**/*"
    Boolean isPullRequest = false

    List<String> emails = []

    Job build(DslFactory dslFactory) {
        dslFactory.job(name) {
            it.description this.description
            logRotator {
                numToKeep 50
            }

            parameters {
                stringParam('VERSION', '10.2.0.0', '')
                stringParam('BRANCH', 'master', '')
            }

            // todo: reusable blocks -> put to one parent class

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
                        wipeOutWorkspace true
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
                        wipeOutWorkspace true
                    }
                }

            }

            steps {
                shell('cd setretail10/SetRetail10_Utils/testStand/SapWSEmulator; export LANG=ru_RU.UTF-8;')
                shell('''mkdir -p "\$WORKSPACE/gradle/wrapper"; cp /opt/gradle/wrapper/* \$WORKSPACE/gradle/wrapper || true; cp /opt/gradlew \$WORKSPACE/gradlew || true;''')
                gradle('clean build',
                        '-xtest',
                        true) {
                    it / wrapperScript('gradlew')
                    it / rootBuildScriptDir('\$workspace/setretail10/SetRetail10_Utils/testStand/SapWSEmulator')
                    it / fromRootBuildScriptDir(false)
                    it / makeExecutable(true)
                }
            }

            publishers {
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
