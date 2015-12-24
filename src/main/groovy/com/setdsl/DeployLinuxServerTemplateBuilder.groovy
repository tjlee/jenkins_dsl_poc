package com.setdsl

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class DeployLinuxServerTemplateBuilder {


    String name
    String description

    Job build(DslFactory dslFactory) {
        dslFactory.multiJob(name) {
            it.description this.description
            logRotator {
                numToKeep 50
            }

            parameters {
                stringParam('VERSION', '10.2.0.0', '')
                stringParam('BRANCH', 'master', '')
                stringParam('IPS', '', '')
                stringParam('SHOP_NUMBER', '', '')
            }

            //build_tgz_flex


            steps {
                phase('Build') {
                    // mb to choose which one to build
                    phaseJob('build_tgz_flex') {
                        currentJobParameters(false)
                        parameters {
                            currentBuild()
                        }
                    }
                }

                phase('Deploy') {
                    phaseJob('deploy_linux') {
                        parameters {
                            currentBuild()
                        }

                        copyArtifacts('upstream') {
                            includePatterns('**/*.sh',)
                            buildSelector {
                                latestSuccessful(true)
                            }
                        }
                    }
                }
            }

        }
    }
}
