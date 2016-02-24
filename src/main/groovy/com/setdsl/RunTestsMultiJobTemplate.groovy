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
                stringParam('VERSION', '10.2.99.0', '')
                stringParam('BRANCH', 'master', 'Server sources github branch or tag')

                choiceParam("STAND", ["C"], '''A - Windows, 172.20.0.140 - Centrum, 172.20.0.141 - Retail, 172.20.0.142 - Centrum cash, 172.20.0.143 - Retail cash
C - Linux, 172.20.0.160 - Centrum, 172.20.0.161 - Retail, 172.20.0.162 - Centrum cash, 172.20.0.163 - Retail cash''')

                stringParam('TEST_SOURCE_BRANCH', 'master', '')
                booleanParam("IS_TO_CONFIG", true, '')
                booleanParam("IS_TO_RUN_ROBOT", true, '')
                booleanParam("IS_TO_RUN_CUCUMBER", false, '')
                booleanParam("IS_TO_RUN_OPER_DAY", false, '')
                booleanParam("IS_TO_RUN_FUNCTIONAL", false, '')
            }

            steps {
                conditionalSteps {
                    condition {
                        stringsMatch("C", "\$STAND", true)
                    }
                    runner('Fail')
                    steps {
                        environmentVariables {
                            env 'VMS_1', '172.20.0.160:linux:standc_server1'
                            env 'VMS_2', '172.20.0.161:linux:standc_server2'
                            env 'CENTRUM_IP', '172.20.0.160'
                            env 'RETAIL_IP', '172.20.0.161'
                            env 'RETAIL_SHOP_NUMBER', '20161'
                            env 'IPS', '172.20.0.162;172.20.0.163'

                        }
                    }
                }

                phase('Build and deploy') {
                    phaseJob('deploy_linux_with_building') {
                        currentJobParameters(false)
                        parameters {
                            currentBuild()

                            predefinedProp('VMS_1', '\$VMS_1')
                            predefinedProp('VMS_2', '\$VMS_2')

                            predefinedProp('CENTRUM_IP', '\$CENTRUM_IP')
                            predefinedProp('RETAIL_IP', '\$RETAIL_IP')
                            predefinedProp('RETAIL_SHOP_NUMBER', '\$RETAIL_SHOP_NUMBER')
                        }
                    }

//                    phaseJob('emulators_build_sap') {
//                        currentJobParameters(false)
//                        parameters {
//                            currentBuild()
//                            /*
//                                stringParam('VERSION', '10.2.0.0', '')
//                stringParam('BRANCH', 'master', '')
//                */
//                        }
//                    }

                    phaseJob('deploy_pos_cash_n_robot') {
                        currentJobParameters(false)
                        parameters {
                            currentBuild()
                            predefinedProp('IPS', '\$IPS')
                        }
                    }
                }

                if (this.isToRunTests) {
                    phase('Run tests') {
                        phaseJob('rus_test_run_without_deployment') {
                            parameters {
                                currentBuild()
                            }

                        }
                    }
                }
            }
        }
    }
}
