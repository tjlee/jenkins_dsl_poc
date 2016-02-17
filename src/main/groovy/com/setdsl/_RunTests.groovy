package com.setdsl

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class _RunTests {

    String name
    String description
    String clientType

    String gitHubTestSourceOwnerAndProject = "crystalservice/autoqa"
    String gitHubTestSourceCheckoutDir = "autoqa"

    String gitHubOwnerAndProject = "crystalservice/setretail10"
    String gitHubCheckoutDir = "setretail10"

    String gitHubCredentials = "4e269209-1b8f-4f0b-a849-c7376ed088e0"


    String isRunOnLinux = true // otherwise windows
    List<String> emails = []


    String startRobotHub =
            '''mkdir -p \$WORKSPACE/autoqa/setrobothub;
mv \$WORKSPACE/setrobothub.zip \$WORKSPACE/autoqa/setrobothub;
mv \$WORKSPACE/setrobothub.zip \$WORKSPACE/autoqa/setrobothub;
unzip \$WORKSPACE/setrobothub.zip -d \$WORKSPACE/autoqa/setrobothub;
mv \$WORKSPACE/autoqa/setrobothub/catalog-goods-robot.xml \$WORKSPACE/autoqa/SetTester/src/test/resources/import;
cd \$WORKSPACE/autoqa/setrobothub;
java -cp lib/*;* ru.crystals.setrobot.hub.SetRobotHub;
'''

    String startSapEmulator =
            '''mv \$WORKSPACE/setretail10/SetRetail10_Utils/testStand/SapWSEmulator/build/libs \$WORKSPACE/autoqa;
mv \$WORKSPACE/autoqa/libs \$WORKSPACE/autoqa/SAP_Emu;
java -jar \$WORKSPACE/autoqa/SAP_Emu/SapWSEmulator.jar;
'''

    String killAllScript = '''kill \\$(jps -lv | grep 'SapWSEmulator' | cut -d ' ' -f 1); kill \\$(jps -lv | grep 'SetRobotHub' | cut -d ' ' -f 1); '''


    String killDBConnections = '''PGPASSWORD=postgres;
psql -U postgres -c "SELECT pg_terminate_backend(procpid)  FROM pg_stat_activity WHERE procpid <> pg_backend_pid();";
psql -h 127.0.0.1 -p 5432 -U postgres -c "drop database sap";'''

    String pingAll = '''ping -n 5 $CENTRUM_IP | grep \'TTL=\' 2>nul && echo \'Connection exists\' || exist 1;
ping -n 5 $RETAIL_IP | grep 'TTL=' 2>nul && echo 'Connection exists' || exist 1;
ping -n 5 $VCASH_NUMBER | grep 'TTL=' 2>nul && echo 'Connection exists' || exist 1;
ping -n 5 $CASH_IP | grep 'TTL=' 2>nul && echo 'Connection exists' || exist 1;
'''

    String copyWrapper = '''
mkdir -p "\$WORKSPACE/gradle/wrapper";
cp \$JENKINS_HOME/userContent/wrapper/* \$WORKSPACE/gradle/wrapper || true;
cp \$JENKINS_HOME/userContent/gradlew \$WORKSPACE/gradlew || true;
cp \$JENKINS_HOME/userContent/gradlew.bat \$WORKSPACE/gradlew.bat || true;
                    '''

    Job build(DslFactory dslFactory) {
        dslFactory.job(name) {
            it.description this.description
            logRotator {
                artifactDaysToKeep(14)
                numToKeep 28
            }

            parameters {
//                stringParam('VERSION', '10.2.0.0', '')
//                stringParam('BRANCH', 'master', '')
                stringParam('TEST_SOURCE_BRANCH', 'master', '')

                booleanParam("IS_TO_CONFIG", false, '')
                booleanParam("IS_TO_RUN_ROBOT", false, '')
                booleanParam("IS_TO_RUN_CUCUMBER", false, '')
                booleanParam("IS_TO_RUN_OPER_DAY", false, '')
                booleanParam("IS_TO_RUN_FUNCTIONAL", false, '')

//                stringParam('CENTRUM_IP', '', '')
//                stringParam('VSHOP_NUMBER', '', '')
//                stringParam('RETAIL_IP', '', '')
//                stringParam('SHOP_NUMBER', '', '')
//                stringParam('CASH_IP', '', '')
//                stringParam('CASH_NUMBER', '', '')
//                stringParam('VCASH_IP', '', '')
//                stringParam('VCASH_NUMBER', '', '')
//
//                // map to properties file
//
//                stringParam('TEST_SUITE', '', '') // todo: make choice param
//                stringParam('TEST_LIST', '', '')
            }

            multiscm {

//                git {
//                    remote {
//                        github(this.gitHubOwnerAndProject)
//                        credentials(this.gitHubCredentials)
//                        branch('\$BRANCH')
//                        refspec('+refs/heads/*:refs/remotes/origin/*')
//                    }
//
//                    cloneTimeout 20
//                    relativeTargetDir(this.gitHubCheckoutDir)
////                    wipeOutWorkspace true
//                }

                git {
                    remote {
                        github(this.gitHubTestSourceOwnerAndProject)
                        credentials(this.gitHubCredentials)
                        branch('\$TEST_SOURCE_BRANCH')
                        refspec('+refs/heads/*:refs/remotes/origin/*')
                    }

                    cloneTimeout 20
                    relativeTargetDir(this.gitHubTestSourceCheckoutDir)
//                    wipeOutWorkspace true
                }
            }



            steps {
                // enables for linux stand
                // hardcoded but need config file
                environmentVariables {
                    env 'CENTRUM_IP', '172.20.0.160'
                    env 'VSHOP_NUMBER', '20160'
                    env 'RETAIL_IP', '172.20.0.161'
                    env 'SHOP_NUMBER', '20161'
                    env 'VCASH_NUMBER', '172.20.0.162'
                    env 'VCASH_IP', '60'
                    env 'CASH_IP', '172.20.0.163'
                    env 'CASH_NUMBER', '61'

                    env 'TEST_LIST', 'CHECKLIST'
                    env 'CUCUMBER', ''
                }

//                shell(this.killAllScript)
//                shell(this.killDBConnections)
//                shell(this.pingAll)
//                shell(this.startRobotHub)
//                shell(this.startSapEmulator)
//                shell(this.copyWrapper)

                String suiteList = ''

                conditionalSteps {
                    condition {
                        booleanCondition('\$IS_TO_CONFIG')
                    }
                    runner('Unstable')
                    steps {
                        environmentVariables {
                            suiteList += 'suite_robot_config_server.xml,suite_robot_config_cash.xml'
//                            env 'TEST_SUITE', suiteList
                        }
                    }
                }

                conditionalSteps {
                    condition {
                        booleanCondition('\$IS_TO_RUN_ROBOT')
                    }
                    runner('Unstable')
                    steps {
                        environmentVariables {
                            suiteList += ',suite_robot_tests.xml'
//                            env 'TEST_SUITE', suiteList
                        }
                    }
                }

                conditionalSteps {
                    condition {
                        booleanCondition('\$IS_TO_RUN_OPER_DAY')
                    }
                    runner('Unstable')
                    steps {
                        environmentVariables {
                            suiteList += ',suite_all_tests.xml'
//                            env 'TEST_SUITE', suiteList
                        }
                    }
                }

                conditionalSteps {
                    condition {
                        booleanCondition('\$IS_TO_RUN_FUNCTIONAL')
                    }
                    runner('Unstable')
                    steps {
                        environmentVariables {
                            suiteList += ',suite_transport.xml,suite_goods_processing.xml,'
//                            env 'TEST_SUITE', suiteList
                        }
                    }
                }

                if (suiteList.startsWith(',')){
                    suiteList = suiteList.substring(1)
                }
                if(suiteList.endsWith(',')){
                    suiteList = suiteList.substring(0, suiteList.length())
                }

                environmentVariables {
                    env 'TEST_SUITE', suiteList
                }

                // UNIX only
//                gradle('clean test \$CUCUMBER',
//                        '''--continue
//-Ptest_suite=\$TEST_SUITE
//-Dtest_centrum_host=$CENTRUM_IP
//-Dtest_retail_host=$RETAIL_IP
//-Dtest_virtualshop_number=$VSHOP_NUMBER
//-Dtest_shop_number=$SHOP_NUMBER
//-Dtest_os_type=UNIX
//-Dtest_virtual_cash_ip=$VCASH_NUMBER
//-Dtest_virtual_cash_number=$VCASH_IP
//-Dtest_cash_ip=$CASH_IP
//-Dtest_cash_number=$CASH_NUMBER
//-Dtest_robot_tests=\$TEST_LIST
//-Dtest_smb_username=jboss
//-Dtest_smb_password=jboss
//-Dtest_smb_server_standalone=/jboss/standalone/
//-Dtest_smb_server_fileimport=/jboss/
//-Dtest_os_username=root
//-Dtest_os_password=324012
//                            ''',
//                        true) {
//                    it / wrapperScript('gradlew')
//                    it / rootBuildScriptDir('\$WORKSPACE/' + this.gitHubTestSourceCheckoutDir + '/SetTester/')
//                    it / fromRootBuildScriptDir(false)
//                    it / makeExecutable(true)
//                }


                shell('\$TEST_SUITE')

            }
        }
    }
}

/**
 *
 * suite_robot.xml,suite_all_tests.xml,suite_transport.xml,suite_goods_processing.xml
 *
 suite_robot_tests.xml
 *
 *
 * --continue
 -Ptest_suite=$TEST_SUITE
 -Dtest_centrum_host=172.20.0.140
 -Dtest_retail_host=172.20.0.141
 -Dtest_virtualshop_number=20140
 -Dtest_shop_number=20141
 -Dtest_os_type=WIN
 -Dtest_virtual_cash_ip=172.20.0.142
 -Dtest_virtual_cash_number=40
 -Dtest_cash_ip=172.20.0.143
 -Dtest_cash_number=41
 -Dtest_robot_tests=$TEST_LIST
 */

/**
 * TEST_LIST=CHECKLIST
 TEST_SUITE=suite_robot.xml,suite_all_tests.xml
 */

/**
 * TEST_LIST=CHECKLIST,-EXCLUDED_BELARUS
 TEST_SUITE=suite_belarus.xml
 */

/**
 * TEST_LIST=CHECKLIST,-EXCLUDED_LENTA
 TEST_SUITE=suite_lenta.xml,suite_all_tests.xml
 */

// TEST_SUITE=suite_robot.xml,suite_all_tests.xml

/**
 * <suite-file path="suite_robot_config_server.xml" />
 <suite-file path="suite_robot_config_cash.xml" />
 <suite-file path="suite_robot_tests.xml" />
 */

//TEST_LIST=CHECKLIST