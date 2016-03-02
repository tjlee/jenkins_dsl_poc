package com.setdsl

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class _RunTests {

    String name
    String description
    String clientType

    String gitHubTestSourceOwnerAndProject = "crystalservice/autoqa"
    String gitHubTestSourceCheckoutDir = "autoqa"

    String gitHubCredentials = "4e269209-1b8f-4f0b-a849-c7376ed088e0"


    String isRunOnLinux = true // otherwise windows
    List<String> emails = []


    String setUpRobotHub =
            '''rm -rf \$WORKSPACE/autoqa/setrobothub || true;
mkdir -p \$WORKSPACE/autoqa/setrobothub;
mv -f \$WORKSPACE/setrobothub.zip \$WORKSPACE/autoqa/setrobothub;
unzip \$WORKSPACE/autoqa/setrobothub/setrobothub.zip -d \$WORKSPACE/autoqa/setrobothub;
cp -f \$WORKSPACE/autoqa/setrobothub/catalog-goods-robot.xml \$WORKSPACE/autoqa/SetTester/src/test/resources/import;
echo "java -cp \$WORKSPACE/autoqa/setrobothub/lib/SetRobot.jar:\$WORKSPACE/autoqa/setrobothub/lib/* ru.crystals.setrobot.hub.SetRobotHub;" > \$WORKSPACE/autoqa/setrobothub/robot.sh;
chmod 555 \$WORKSPACE/autoqa/setrobothub/robot.sh;
'''

    String runRobotHub = '''daemonize -E $BUILD_ID=dontKillMe \$WORKSPACE/autoqa/setrobothub/robot.sh;'''

    String startSapEmulator =
            '''mv \$WORKSPACE/setretail10/SetRetail10_Utils/testStand/SapWSEmulator/build/libs \$WORKSPACE/autoqa;
mv \$WORKSPACE/autoqa/libs \$WORKSPACE/autoqa/SAP_Emu;
java -jar \$WORKSPACE/autoqa/SAP_Emu/SapWSEmulator.jar;
'''

    String killAllScript = '''kill $(jps -lv | grep 'SapWSEmulator' | cut -d ' ' -f 1) || true; kill $(jps -lv | grep 'SetRobotHub' | cut -d ' ' -f 1) || true;'''


    String killDBConnections = '''PGPASSWORD=postgres;
psql -U postgres -c "SELECT pg_terminate_backend(procpid)  FROM pg_stat_activity WHERE procpid <> pg_backend_pid();";
psql -h 127.0.0.1 -p 5432 -U postgres -c "drop database sap";'''

    String pingAll = '''ping -n 5 $CENTRUM_IP | grep \'TTL=\' 2>nul && echo \'Connection exists\' || exit 1;
ping -n 5 $RETAIL_IP | grep 'TTL=' 2>nul && echo 'Connection exists' || exit 1;
ping -n 5 $VCASH_NUMBER | grep 'TTL=' 2>nul && echo 'Connection exists' || exit 1;
ping -n 5 $CASH_IP | grep 'TTL=' 2>nul && echo 'Connection exists' || exit 1;
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
                stringParam('TEST_SOURCE_BRANCH', 'master', '')

                booleanParam("IS_TO_CONFIG", true, '')
                booleanParam("IS_TO_RUN_ROBOT", true, '')
                booleanParam("IS_TO_RUN_CUCUMBER", false, '')
                booleanParam("IS_TO_RUN_OPER_DAY", false, '')
                booleanParam("IS_TO_RUN_FUNCTIONAL", false, '')

                stringParam('CENTRUM_IP', '172.20.0.160', '')
                stringParam('VSHOP_NUMBER', '20160', 'Virtual shop number')
                stringParam('RETAIL_IP', '172.20.0.161', '')
                stringParam('SHOP_NUMBER', '20161', 'Retail shop number')
                stringParam('CASH_IP', '172.20.0.163', 'Retail cash ip')
                stringParam('CASH_NUMBER', '61', 'Retail cash number')
                stringParam('VCASH_IP', '172.20.0.162', 'Virtual shop cash ip')
                stringParam('VCASH_NUMBER', '60', 'Virtual shop cash number')

                choiceParam('TEST_LIST', ["CHECKLIST", "CHECKLIST,-EXCLUDED_BELARUS", "CHECKLIST,-EXCLUDED_LENTA", "EXCLUDED_BELARUS", "EXCLUDED_LENTA"])

//                stringParam('TEST_SUITE', '', '')
            }

            multiscm {
                git {
                    remote {
                        github(this.gitHubTestSourceOwnerAndProject)
                        credentials(this.gitHubCredentials)
                        branch('\$TEST_SOURCE_BRANCH')
                        refspec('+refs/heads/*:refs/remotes/origin/*')
                    }

                    cloneTimeout 20
                    relativeTargetDir(this.gitHubTestSourceCheckoutDir)
                }
            }


            steps {

                copyArtifacts('deploy_pos_cash_n_robot') {
                    includePatterns('*.zip',)
                    buildSelector {
                        latestSuccessful(true)
                    }
                }

                // enables for linux stand
                // hardcoded but need config file
                environmentVariables {
//                    env 'TEST_LIST', 'CHECKLIST'
                    env 'CUCUMBER', ''
                    env 'TEST_SUITE', ''
                    env 'TEST_SUITE1', ''
                    env 'TEST_SUITE2', ''
                    env 'TEST_SUITE3', ''
                }

                shell(this.copyWrapper)
                shell(this.killAllScript)
//                shell(this.killDBConnections)
//                shell(this.pingAll)
                shell(this.setUpRobotHub)
                shell(this.runRobotHub)
//                shell(this.startSapEmulator)


                conditionalSteps {
                    condition {
                        booleanCondition('\$IS_TO_CONFIG')
                    }
                    runner('Unstable')
                    steps {
                        environmentVariables {
                            env 'TEST_SUITE', ',suite_robot_config_server.xml,suite_robot_config_cash.xml'
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
                            env 'TEST_SUITE1', ',suite_robot_tests.xml'
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
                            env 'TEST_SUITE2', ',suite_all_tests.xml'
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
                            env 'TEST_SUITE3', ',suite_transport.xml,suite_goods_processing.xml'
                        }
                    }
                }

                conditionalSteps {
                    condition {
                        booleanCondition('\$IS_TO_RUN_CUCUMBER')
                    }
                    runner('Unstable')
                    steps {
                        environmentVariables {
                            env 'CUCUMBER', 'cucumber'
                        }
                    }
                }

                // UNIX only
                gradle('clean test $CUCUMBER',
                        '''--continue
-Ptest_suite=$TEST_SUITE$TEST_SUITE1$TEST_SUITE2$TEST_SUITE3
-Dtest_centrum_host=$CENTRUM_IP
-Dtest_retail_host=$RETAIL_IP
-Dtest_virtualshop_number=$VSHOP_NUMBER
-Dtest_shop_number=$SHOP_NUMBER
-Dtest_os_type=UNIX
-Dtest_virtual_cash_ip=$VCASH_NUMBER
-Dtest_virtual_cash_number=$VCASH_IP
-Dtest_cash_ip=$CASH_IP
-Dtest_cash_number=$CASH_NUMBER
-Dtest_robot_tests=$TEST_LIST
-Dtest_smb_username=jboss
-Dtest_smb_password=jboss
-Dtest_smb_server_standalone=/jboss/standalone/
-Dtest_smb_server_fileimport=/jboss/
-Dtest_os_username=root
-Dtest_os_password=324012
                            ''',
                        true) {
                    it / wrapperScript('gradlew')
                    it / rootBuildScriptDir('\$WORKSPACE/' + this.gitHubTestSourceCheckoutDir + '/SetTester/')
                    it / fromRootBuildScriptDir(false)
                    it / makeExecutable(true)
                }

                shell(this.killAllScript)

            }

            publishers {

                archiveXUnit {
                    jUnit {
                        pattern('**/test-results/*.xml,**/cucumber/junit-report.xml')
                    }

                    failedThresholds {
                        unstable(10)
                        unstableNew(10)
                        failure(10)
                        failureNew(10)
                    }

                    skippedThresholds {
                        unstable(5)
                        unstableNew(5)
                        failure(5)
                        failureNew(5)
                    }

                    thresholdMode(ThresholdMode.NUMBER)
                }


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
