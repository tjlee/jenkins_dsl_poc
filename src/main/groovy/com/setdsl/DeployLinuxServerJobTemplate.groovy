package com.setdsl

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class DeployLinuxServerJobTemplate {


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
                            predefinedProp('FLEX_DEBUG', 'true')
                            predefinedProp('FLEX_TEST_MODE', 'true')


                        }
                    }
                }

                phase('Restore vm state') {
                    phaseJob('restore_virtual_pc_state') {
                        currentJobParameters(false)
                        parameters {
                            predefinedProp('VMS', '172.20.0.160:linux:standc_server1')
                        }
                    }

                    phaseJob('restore_virtual_pc_state') {
                        currentJobParameters(false)
                        parameters {
                            currentBuild()
                            predefinedProp('VMS', '172.20.0.161:linux:standc_server2')
                        }
                    }
                }

                phase('Deploy') {
                    phaseJob('deploy_linux') {
                        parameters {
                            predefinedProp('IP', '172.20.0.160')
                            predefinedProp('SHOP_NUMBER', '0')
                        }

                        copyArtifacts('build_tgz_flex') {
                            includePatterns('**/*.sh',)
                            buildSelector {
                                latestSuccessful(true)
                            }
                        }
                    }

                    phaseJob('deploy_linux') {
                        parameters {
                            predefinedProp('IP', '172.20.0.161')
                            predefinedProp('SHOP_NUMBER', '20161')
                        }

                        copyArtifacts('build_tgz_flex') {
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
