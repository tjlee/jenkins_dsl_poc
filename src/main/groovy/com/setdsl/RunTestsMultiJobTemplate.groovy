package com.setdsl

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job


/**
 * WHOLE STAND REDEPLOY AND TEST
 */


class RunTestsMultiJobTemplate {
    String name
    String description
    Boolean isToRunTests = true

    Job build(DslFactory dslFactory) {
        dslFactory.multiJob(name) {
            it.description this.description
            logRotator {
                numToKeep 20
            }

            parameters {
                stringParam('VERSION', '10.2.0.0', '')
                stringParam('BRANCH', 'master', '')

                stringParam('SHOP_NUMBER', '', '')
                stringParam('TEST_SOURCE_BRANCH', 'master', '')

            }

            //build_tgz_flex

            // prepare parameters
            // how to pass all required parameters


            steps {
                phase('Build and deploy') {
                    phaseJob('deploy_linux_with_building') {
                        currentJobParameters(false)
                        parameters {
                            currentBuild()

                            /*
                            *
                stringParam('VERSION', '10.2.0.0', '')
                stringParam('BRANCH', 'master', '')

                stringParam('VMS_1', '172.20.0.160:linux:standc_server1')
                stringParam('VMS_2', '172.20.0.161:linux:standc_server2')

                stringParam('CENTRUM_IP', '172.20.0.160')

                stringParam('RETAIL_IP', '172.20.0.161')
                stringParam('RETAIL_SHOP_NUMBER', '20161')
                            *
                            * */

                            predefinedProp('IPS', '')
                            predefinedProp('SHOP_NUMBER', '20161')

                        }
                    }

                    phaseJob('emulators_build_sap') {
                        currentJobParameters(false)
                        parameters {
                            currentBuild()
                            /*
                                stringParam('VERSION', '10.2.0.0', '')
                stringParam('BRANCH', 'master', '')
                */
                        }
                    }

                    phaseJob('deploy_pos_cash_n_robot') {
                        currentJobParameters(false)
                        parameters {
                            currentBuild()
                            predefinedProp('IPS', '172.20.0.162;172.20.0.163')

                            /*
                             stringParam('VERSION', '10.2.0.0', '')
                stringParam('BRANCH', 'master', '')
                if (this.isToDeployCash) {
                    stringParam('IPS', '', 'Divide ips by ; ')
                }
                            * */

                        }
                    }
                }

                if (this.isToRunTests) {
                    phase('Run tests') {
                        phaseJob('rus_test_run_without_deployment') {
                            parameters {
                                currentBuild()

                                /*
                                  stringParam('VERSION', '10.2.0.0', '')
                                  stringParam('BRANCH', 'master', '')
                                  stringParam('TEST_SOURCE_BRANCH', 'master', '')
                    */
                                // centrum ip hardcoded mother fucker need at leas active choise
                            }

                            copyArtifacts('deploy_pos_cash_n_robot') {
                                includePatterns('**/*.zip',)
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
}
