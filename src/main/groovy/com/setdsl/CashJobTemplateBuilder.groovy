package com.setdsl

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

/**
 * Build Cash Job Template
 */

class CashJobTemplateBuilder {

    String packRobot = '''
cd \$WORKSPACE/setretail10/SetRetail10_Utils/testStand/SetRobot/setrobot-core/build/data/setrobothub/;
zip -r setrobothub.zip ./*;
mv -f -v \$WORKSPACE/setretail10/SetRetail10_Utils/testStand/SetRobot/setrobot-core/build/data/setrobothub/setrobothub.zip \$WORKSPACE/;
'''


    String rebootScript = '''
check_ping()
{
        ping -c 1 $IP | grep from && return 1 || return 0
}
check_no_ping()
{
        ping -c 1 $IP | grep from && return 0 || return 1
}
post_err_after_reboot()
{
        echo ERROR!!! After reboot cash with ip $IP not start
        exit 1
}
post_err_reboot()
{
        echo ERROR!!! After command reboot cash with ip $IP not rebooted
        exit 1
}
cash_reboot()
{
echo Send command reboot cash with ip $IP
        sshpass -p "324012" ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no tc@$IP "cash reboot"
}

for IP in $IPS; do

if check_ping; then
    echo Connections to cash with ip $IP is failed.
else
    echo Connections to cash with ip $IP is succes.
fi

if cash_reboot; then
    echo Reboot cash with ip $IP is failed.
else
    echo Reboot cash with ip $IP is succes.
fi

CNT=0
while check_no_ping; do
        let CNT=$CNT+1
        [ $CNT -gt 90 ] && post_err_reboot
        let TIME=$CNT*2
        echo Wait disconnect to cash with ip $IP. $TIME sec.
        sleep 2
done

CNT=0
while check_ping; do
        let CNT=$CNT+1
        [ $CNT -gt 36 ] && post_err_after_reboot
        let TIME=$CNT*5
        echo Wait up link to cash with ip $IP. $TIME sec.
        sleep 5
done

echo Reboot cash with ip $IP is finished.

echo Wait then cash loading finished.

sleep 10

echo Complete!

done
'''

// Lenta POS Belarus
    String deployCashScript = '''
#unpack tar files
mkdir $WORKSPACE/crystal-cash/
mkdir $WORKSPACE/crystal-conf/
cd $WORKSPACE/$CASH_TYPE
tar xvf crystal-cash.tar -C $WORKSPACE/crystal-cash/
tar xvf crystal-conf.tar -C $WORKSPACE/crystal-conf/
# deploy cashes, listed in $IPS
for IP in $IPS; do
 #stop cash
 sshpass -p "324012" ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no tc@$IP "cash stop"
 #clear cash
 sshpass -p "324012" ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no tc@$IP "rm -fr storage/crystal-cash/*"
 sshpass -p "324012" ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no tc@$IP "rm -fr storage/crystal-conf/*"
 #copy cash
 sshpass -p "324012" scp -r -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $WORKSPACE/crystal-cash/* tc@$IP:storage/crystal-cash
 sshpass -p "324012" scp -r -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $WORKSPACE/crystal-conf/* tc@$IP:storage/crystal-conf
 sshpass -p "324012" ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no tc@$IP "rm -fr storage/drop*"
 sshpass -p "324012" scp -r -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $JENKINS_HOME/userContent/drop* tc@$IP:storage
 #drop db
 sshpass -p "324012" ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no tc@$IP "cd storage; ./drop.sh";
done
'''

    String deployRobotScript = '''
check_process()
{
         sshpass -p "324012" ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no tc@$IP "ps -elf | grep -v grep | grep Loader && return 1 || return 0"
}
check_port()
{
        nc -w2 $IP 8000 && return 1 || return 0
}
check_ping()
{
        ping -c 1 $IP | grep from && return 1 || return 0
}
post_err_process()
{
\techo Installation failed because a cash on $IP does not started in the allotted time $TIME sec.
        exit 1
}
post_err_link()
{
\techo Installation failed because a robot on $IP does not listen linkport in the allotted time $TIME sec.
        exit 1
}
post_err_ping()
{
        echo Installation failed because a computer $IP does not responding in the allotted time $TIME sec.
        exit 1
}
debug_on()
{
         echo Change start string in start.sh.
         sshpass -p "324012" ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no tc@$IP "sed -i -e 's/# *java -Xdebug/java -Xdebug/g' -e 's/^ *java -Djava.net.preferIPv4Stack=true/#java -Djava.net.preferIPv4Stack=true/g' /home/tc/storage/crystal-cash/start.sh"
         echo Hard kill java process.
         sshpass -p "324012" ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no tc@$IP "killall -9 java"
         sshpass -p "324012" ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no tc@$IP "sleep 5"
         echo Start cash on $IP.
         sshpass -p "324012" ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no tc@$IP "cash start"
}

#deploy setrobot to cashes
cd $WORKSPACE/setretail10/SetRetail10_Utils/testStand/SetRobot/setrobot-core
export LANG=ru_RU.UTF-8
gradle clean deployRobot -xtest -PtypeProduct=$ROBOT_TYPE -PcashIPs=`echo $IPS | xargs | sed 's/ /;/'`


# Check cashes and SetRobot up, listed in $IPS
for IP in $IPS; do

echo Check cash and robot on $IP working

CNT=0
while check_ping; do
        let CNT=$CNT+1
        [ $CNT -gt 20 ] && post_err_ping
        let TIME=$CNT*3
        echo Wait for boot cash $IP. $TIME sec.
        sleep 3
done

CNT=0
while check_process; do
        let CNT=$CNT+1
        [ $CNT -gt 30 ] && post_err_process
        let TIME=$CNT*3
        echo Wait for cash start on $IP. $TIME sec.
        sleep 3
done

CNT=0
while check_port; do
        let CNT=$CNT+1
        [ $CNT -gt 30 ] && post_err_link
        let TIME=$CNT*3
        echo Wait for robot start on $IP. $TIME sec.
        sleep 3
done

done

#zalepon. What then cash creat DB on ferst start
sleep 60

#start cash on debug
for IP in $IPS; do
debug_on
done

# Check cashes and SetRobot up, listed in $IPS
for IP in $IPS; do

CNT=0
while check_process; do
        let CNT=$CNT+1
        [ $CNT -gt 30 ] && post_err_process
        let TIME=$CNT*3
        echo Wait for cash start on $IP. $TIME sec.
        sleep 3
done

CNT=0
while check_port; do
        let CNT=$CNT+1
        [ $CNT -gt 30 ] && post_err_link
        let TIME=$CNT*3
        echo Wait for robot start on $IP. $TIME sec.
        sleep 3
done

echo Start Cash and robot on $IP success.

done
'''

    String name
    String description

    String gitHubCredentials = "4e269209-1b8f-4f0b-a849-c7376ed088e0"

    String gitHubOwnerAndProject = "crystalservice/setretail10"
    String gitHubCheckoutDir = "setretail10"

    String gitHubOwnerAndProjectLinuxSources = "crystalservice/setretail10linux"
    String gitHubCheckoutDirLinuxSources = "setretail10linux"

    String buildType /* tar or iso*/
    String clientType /*default lenta belarus*/ /*POS, Lenta, Belarus*/

    Boolean isPullRequest = false
    Boolean isToPublishUnitTests = true

    Boolean isToDeployCash = false
    Boolean isToDeployRobot = false


    String artifacts = 'POS/**/*,Lenta/**/*,Belarus/**/*,cash.branch,*.iso,*.zip'

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
                stringParam('IPS', '', '')
            }


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
//                        wipeOutWorkspace true
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
//                        wipeOutWorkspace true
                    }
                }
                // if iso need to checkout linux repo
                if (this.buildType == "iso") {
                    git {
                        remote {
                            github(this.gitHubOwnerAndProjectLinuxSources)
                            credentials(this.gitHubCredentials)
                            branch('master')
                            refspec('+refs/heads/*:refs/remotes/origin/*')
                        }

                        cloneTimeout 20
                        relativeTargetDir(this.gitHubCheckoutDirLinuxSources)
//                        wipeOutWorkspace true

                    }
                }
            }


            steps {

                shell('cd' + this.gitHubCheckoutDir + '/SetRetail10_Cash/buildGradle/; export LANG=ru_RU.UTF-8;')

                shell('''mkdir -p "\$WORKSPACE/gradle/wrapper"; cp /opt/gradle/wrapper/* \$WORKSPACE/gradle/wrapper || true; cp /opt/gradlew \$WORKSPACE/gradlew || true;''')

                gradle('clean build tarAll',
                        ' -PpresetCashParamsPath=\$JENKINS_HOME/userContent/cashes.xml -PproductVersion=\$VERSION -xtest -Pbranch=\$GIT_BRANCH -Pshaid=\$GIT_COMMIT',
                        true) {
                    it / wrapperScript('gradlew')
                    it / makeExecutable(true)
                    it / fromRootBuildScriptDir(false)
                    it / rootBuildScriptDir('\$WORKSPACE/' + this.gitHubCheckoutDir + '/SetRetail10_Cash/buildGradle/')
                }

                shell('cp -f -r -a \$WORKSPACE/' + this.gitHubCheckoutDir + '/SetRetail10_Cash/buildGradle/build/distributions/{POS,Lenta,Belarus} \$WORKSPACE/')
                shell('cp -f -r -a \$WORKSPACE/' + this.gitHubCheckoutDir + '/SetRetail10_Cash/buildGradle/build/LentaConfigs/cash-configs.tar.gz \$WORKSPACE/Lenta')

                shell(packRobot)

                if (this.buildType == "iso") {

                    shell('cd \$WORKSPACE/' + this.gitHubCheckoutDirLinuxSources + '/cash-tinycore3/;')

                    if (this.clientType == 'pos') {

                        shell('./build.sh -i=set-retail-\$VERSION-pos.iso -d=\$WORKSPACE -s=\$WORKSPACE/POS/crystal-cash.tar -c=\$WORKSPACE/POS/crystal-conf.tar -z=yes -v=java7')

                    } else if (this.clientType == 'lenta') {

                        shell('./build.sh -i=set-retail-$VERSION-lenta.iso -d=$WORKSPACE -s=$WORKSPACE/Lenta/crystal-cash.tar -c=$WORKSPACE/Lenta/crystal-conf.tar -z=yes -v=java7 -l=$WORKSPACE/Lenta/cash-configs.tar.gz')

                    } else if (this.clientType == 'belarus') {

                        shell(' ./build.sh -i=set-retail-$VERSION-belarus.iso -d=$WORKSPACE -s=$WORKSPACE/Belarus/crystal-cash.tar -c=$WORKSPACE/Belarus/crystal-conf.tar -z=yes -v=java7')

                    }
                }

                // todo: remove shit shit and put scripts into files, remove hard code
                if (this.isToDeployCash || this.isToDeployRobot) {
                    // reboot
//                    inject {
                    // i hope it works )))
                    environmentVariables {
                        env 'IPS', '\$IPS'
                        if (this.clientType == 'pos') {
                            env 'CASH_TYPE', 'POS'
                            env 'ROBOT_TYPE', 'pos'
                        } else if (this.clientType == 'lenta') {
                            env 'CASH_TYPE', 'Lenta'
                            env 'ROBOT_TYPE', 'Lenta'
                        } else if (this.clientType == 'belarus') {
                            env 'CASH_TYPE', 'Belarus'
                            env 'ROBOT_TYPE', 'posBelarus'
                        }
                    }
//                    }

                    if (this.isToDeployCash) {
                        shell(rebootScript)
                        shell(deployCashScript)
                    }

                    if (this.isToDeployRobot) {
                        shell(deployRobotScript)
                    }

                }

                // as we can deploy robot aside


            }

            publishers {
                if (this.isToPublishUnitTests) {
                    archiveJunit('**/test-results/*.xml')
                }

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