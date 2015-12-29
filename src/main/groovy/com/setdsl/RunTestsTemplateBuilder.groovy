package com.setdsl

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class RunTestsTemplateBuilder {

    String name
    String description
    String clientType

    String gitHubTestSourceOwnerAndProject = "crystalservice/autoqa"
    String gitHubTestSourceCheckoutDir = "autoqa"

    String gitHubOwnerAndProject = "crystalservice/setretail10"
    String gitHubCheckoutDir = "setretail10"

    String gitHubCredentials = "31df12ac-5d1f-495d-99fe-ad351505d316"


    Boolean isToConfig = true
    Boolean isToRunRobot = false
    Boolean isToRunOperday = false
    Boolean isToRunFunctional = false
    Boolean isToRunCucumber = false


    String isRunOnLinux = true // otherwise windows

    List<String> emails = []

    Job build(DslFactory dslFactory) {

/**
 * here we need to build cash and sap emu
 */
        dslFactory.job(name) {
            it.description this.description
            logRotator {
                numToKeep 50
            }

//            String stand_a_config = readFileFromWorkspace('./resources/stand_a.config')

            parameters {
                stringParam('VERSION', '10.2.0.0', '')
                stringParam('SERVER_BRANCH', 'master', '')
                stringParam('TEST_SOURCE_BRANCH', 'master', '')

//                choiceParam('CONFIG_FILE', ['a', 'c'], '')

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
//                stringParam('TEST_SUITE', '', '') // todo: need properties file! // todo: or hardcoded?
//                stringParam('TEST_LIST', '', '')
            }

            multiscm {

                git {
                    remote {
                        github(this.gitHubOwnerAndProject)
                        credentials(this.gitHubCredentials)
                        branch('\$SERVER_BRANCH')
                        refspec('+refs/heads/*:refs/remotes/origin/*')
                    }

                    cloneTimeout 20
                    relativeTargetDir(this.gitHubCheckoutDir)
                    wipeOutWorkspace true
                }

                git {
                    remote {
                        github(this.gitHubTestSourceOwnerAndProject)
                        credentials(this.gitHubCredentials)
                        branch('\$TEST_SOURCE_BRANCH')
                        refspec('+refs/heads/*:refs/remotes/origin/*')
                    }

                    cloneTimeout 20
                    relativeTargetDir(this.gitHubTestSourceCheckoutDir)
                    wipeOutWorkspace true
                }
            }



            steps {


//              todo:  assuming that we got built robot in previous job

//                // build robot actually cash
//                shell("cd '\$WORKSPACE/setretail10/SetRetail10_Utils/testStand/SetRobot/setrobot-core'")
//
//                gradle('clean build', '', true) {
//                    it / wrapperScript('gradlew')
//                }


                //


                // kill shit on agent
                shell("kill \$(jps -lv | grep 'SapWSEmulator' | cut -d ' ' -f 1); kill \$(jps -lv | grep 'SetRobotHub' | cut -d ' ' -f 1); ")

                shell('''PGPASSWORD=postgres;
psql -U postgres -c "SELECT pg_terminate_backend(procpid)  FROM pg_stat_activity WHERE procpid <> pg_backend_pid();";
psql -h 127.0.0.1 -p 5432 -U postgres -c "drop database sap";''')

                shell('''
ping -n 5 172.20.0.160 | grep \'TTL=\' 2>nul && echo \'Connection exists\' || exist 1;
ping -n 5 172.20.0.161 | grep 'TTL=' 2>nul && echo 'Connection exists' || exist 1;
ping -n 5 172.20.0.162 | grep 'TTL=' 2>nul && echo 'Connection exists' || exist 1;
ping -n 5 172.20.0.163 | grep 'TTL=' 2>nul && echo 'Connection exists' || exist 1;
''')

                shell('echo run sup emulator')
                shell('echo run robothub')
//                shell('echo ' + stand_a_config.toString())
                /**
                 * LOCAL_IP ???
                 */

                if (this.isToConfig) { // even if to test :-D

//                    inject { // todo: see this shit => !!! if not configured
                    environmentVariables {
                        env 'TEST_LIST', 'CHECKLIST'
                        env 'TEST_SUITE', 'suite_robot_config_server.xml,suite_robot_config_cash.xml'
                    }
//                    }

                    gradle('clean test',
                            '''
--continue
-Ptest_suite=\$TEST_SUITE
-Dtest_centrum_host=172.20.0.160
-Dtest_retail_host=172.20.0.161
-Dtest_virtualshop_number=20160
-Dtest_shop_number=20161
-Dtest_os_type=UNIX
-Dtest_virtual_cash_ip=172.20.0.162
-Dtest_virtual_cash_number=60
-Dtest_cash_ip=172.20.0.163
-Dtest_cash_number=61
-Dtest_robot_tests=\$TEST_LIST
-Dtest_smb_username=jboss
-Dtest_smb_password=jboss
-Dtest_smb_server_standalone=/jboss/standalone/
-Dtest_smb_server_fileimport=/jboss/
-Dtest_os_username=root
-Dtest_os_password=324012
''',
                            true) {
                        it / wrapperScript('gradlew')
                        it / rootBuildScriptDir('\$workspace/autoqa/SetTester/')
                        it / fromRootBuildScriptDir(false)
                    }

                    if (this.isToRunRobot) {
//                        inject {
                        environmentVariables {
                            env 'TEST_SUITE', 'suite_robot_tests.xml'
                        }
//                        }
                    }

                    // config os and take diff properties files


                }

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

                // external properties files
                // stand_c.properties
                // stand_a.properties

// TEST_SUITE=suite_robot.xml,suite_all_tests.xml

                /**
                 * <suite-file path="suite_robot_config_server.xml" />
                 <suite-file path="suite_robot_config_cash.xml" />
                 <suite-file path="suite_robot_tests.xml" />
                 */

                //TEST_LIST=CHECKLIST


            }


        }
    }

    /**
     *
     cd $WORKSPACE/setretail10/SetRetail10_Utils/testStand/SetRobot/setrobot-core/build/data/setrobothub/
     zip -r setrobothub.zip ./*
     mv -f -v $WORKSPACE/setretail10/SetRetail10_Utils/testStand/SetRobot/setrobot-core/build/data/setrobothub/setrobothub.zip $WORKSPACE/
     */



    /// killing this stuff on test agent

    // kill


    String killAllScript =
            '''

echo "Killing hanging process for launcher.jar, SapWSEmulator and SetRobotHub"
$sapPid = (jps -lv | findstr /r "SapWSEmulator")
$robotHubPid = (jps -lv | findstr /r "SetRobotHub")
$launcherPid = (jps -lv | findstr /r "launcher.jar")

if ($launcherPid -ne $null) {
write-host "Killing launcher process $launcherPid"
$pid3 = $launcherPid.Split(" ")[0]
taskkill /PID $pid3 /F
}

if ($sapPid -ne $null) {
write-host "Killing SapWSEmulator process $sapPid"
$pid2 = $sapPid.Split(" ")[0]
taskkill /PID $pid2 /F
}

if ($robotHubPid -ne $null) {
write-host "Killing SetRobotHubprocess $robotHubPid"
$pid1 = $robotHubPid.Split(" ")[0]
taskkill /PID $pid1 /F
}

'''

    String killDBConnections = '''
chcp 65001
set PGPASSWORD=postgres
psql -U postgres -c "SELECT pg_terminate_backend(procpid)  FROM pg_stat_activity WHERE procpid <> pg_backend_pid();"
psql -h 127.0.0.1 -p 5432 -U postgres -c "drop database sap"
'''

    String healthCheck = '''
chcp 65001
echo Проверяем, что связь со стендом в порядке
ping -n 5 172.16.7.10 | findstr /R "TTL=" 2>nul && echo Connection exist || exit 1
ping -n 5 172.20.0.160 | findstr /R "TTL=" 2>nul && echo Connection exist || exit 1
ping -n 5 172.20.0.161 | findstr /R "TTL=" 2>nul && echo Connection exist || exit 1
ping -n 5 172.20.0.162 | findstr /R "TTL=" 2>nul && echo Connection exist || exit 1
ping -n 5 172.20.0.163 | findstr /R "TTL=" 2>nul && echo Connection exist || exit 1
echo Со связью все ОК
'''

    String robotPreRun1 = '''
chcp 65001
cd %WORKSPACE%\\autoqa
mkdir setrobothub
cd %WORKSPACE%
move %WORKSPACE%\\setrobothub.zip %WORKSPACE%\\autoqa\\setrobothub
move %WORKSPACE%\\setrobothub.properties %WORKSPACE%\\autoqa\\setrobothub
cd %WORKSPACE%\\autoqa\\setrobothub
7z x setrobothub.zip
move %WORKSPACE%\\autoqa\\setrobothub\\catalog-goods-robot.xml %WORKSPACE%\\autoqa\\SetTester\\src\\test\\resources\\import
START /B setrobothub.bat
'''

    String robotPreRun2 = '''
chcp 65001
cd %WORKSPACE%
move SetRetail10_Utils\\testStand\\SapWSEmulator\\build\\libs %WORKSPACE%\\autoqa
cd %WORKSPACE%\\autoqa
rename libs SAP_Emu
cd %WORKSPACE%\\autoqa\\SAP_Emu
Echo java -jar SapWSEmulator.jar > sap_emu.bat
START /B sap_emu.bat
'''


}

/**
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
