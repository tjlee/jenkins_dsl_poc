package com.setdsl

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job


class RunTestsMultiJobTemplate {


    String name
    String description

    Job build(DslFactory dslFactory) {
        dslFactory.multiJob(name) {
            it.description this.description
            logRotator {
                numToKeep 20
            }

            parameters {
//                stringParam('VERSION', '10.2.0.0', '')
//                stringParam('BRANCH', 'master', '')
//                stringParam('IPS', '', '')
//                stringParam('SHOP_NUMBER', '', '')
            }

            //build_tgz_flex


            steps {
                phase('Build and deploy') {
                    phaseJob('deploy_linux_with_building'){
                        currentJobParameters(false)
                        parameters {
                            currentBuild()
                        }
                    }

                    phaseJob('emulators_build_sap') {
                        currentJobParameters(false)
                        parameters {
                            currentBuild()
                        }
                    }

                    phaseJob('deploy_pos_cash_n_robot') {
                        currentJobParameters(false)
                        parameters {
                            currentBuild()
                        }
                    }
                }


                phase('Run tests') {
                    phaseJob('rus_test_run_without_deployment') {
                        parameters {
                            currentBuild()
                        }

                        copyArtifacts('build_pos_cash_tar') {
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
