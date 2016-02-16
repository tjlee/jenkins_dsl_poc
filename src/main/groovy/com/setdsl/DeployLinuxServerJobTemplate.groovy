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

                stringParam('VMS_1', '172.20.0.160:linux:standc_server1')
                stringParam('VMS_2', '172.20.0.161:linux:standc_server2')

                stringParam('CENTRUM_IP', '172.20.0.160')

                stringParam('RETAIL_IP', '172.20.0.161')
                stringParam('RETAIL_SHOP_NUMBER', '20161')

            }


            steps {
                phase('Build') {
                    // mb to choose which one to build
                    phaseJob('build_sh_flex') {
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
                            predefinedProp('VMS', '\$VMS_1')
                        }
                    }

                    phaseJob('restore_virtual_pc_state') {
                        currentJobParameters(false)
                        parameters {
                            currentBuild()
                            predefinedProp('VMS', '\$VMS_2')
                        }
                    }
                }

                phase('Deploy') {
                    phaseJob('deploy_linux') {
                        parameters {
                            predefinedProp('IP', '\$CENTRUM_IP')
                            predefinedProp('SHOP_NUMBER', '0')
                        }

//                        copyArtifacts('build_sh_flex') {
//                            includePatterns('**/*.sh, *.sh, set10install.sh, **/set10install.sh')
//                            buildSelector {
//                                latestSuccessful(true)
//                            }
//                        }
                    }

                    phaseJob('deploy_linux') {
                        parameters {
                            predefinedProp('IP', '\$RETAIL_IP')
                            predefinedProp('SHOP_NUMBER', '\$RETAIL_SHOP_NUMBER')
                        }

//                        copyArtifacts('build_sh_flex') {
//                            includePatterns('**/*.sh, *.sh, set10install.sh, **/set10install.sh')
//                            buildSelector {
//                                latestSuccessful(true)
//                            }
//                        }
                    }
                }

            }

        }
    }
}
