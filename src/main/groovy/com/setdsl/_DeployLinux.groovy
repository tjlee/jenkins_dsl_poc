package com.setdsl

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job


class _DeployLinux {

    String deployLinuxScript = '''
check_ping()
{
        ping -c 1 $IP | grep from && return 1 || return 0
}
check_no_ping()
{
        ping -c 1 $IP | grep from && return 0 || return 1
}
post_err_after_install()
{
        echo ERROR!!! After install Set10 server computer not restart
        exit 1
}
post_err_after_reboot()
{
        echo ERROR!!! After reboot server computer not start
        exit 1
}
post_err_deploy()
{
        echo ERROR!!! Server NOT deployed
        exit 1
}
check_deploy()
{
        echo Check .deployed file exist
        sshpass -p 324012 ssh -o UserKnownHostsFile=/dev/null \\
        -o StrictHostKeyChecking=no root@$IP "ls -l /var/lib/jboss/standalone/deployments/" | grep Set10.ear.deployed && return 1 || return 0
}
sshpass -p "324012" ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@$IP "ntpdate -s -u ntp.ubuntu.com"
sshpass -p "324012" scp -r -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no *.sh root@$IP:/root
sshpass -p "324012" ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@$IP "chmod +x *.sh"
sshpass -p "324012" ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@$IP "./set10install.sh $IP $SHOP_NUMBER"
sshpass -p "324012" ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@$IP "shutdown -r 1"

CNT=0
while check_no_ping; do
        let CNT=$CNT+1
        [ $CNT -gt 90 ] && post_err_after_install
        let TIME=$CNT*2
        echo Wait disconnect to server $IP. $TIME sec.
        sleep 2
done

CNT=0
while check_ping; do
        let CNT=$CNT+1
        [ $CNT -gt 36 ] && post_err_after_reboot
        let TIME=$CNT*5
        echo Wait up link to server $IP. $TIME sec.
        sleep 5
done

CNT=0
while check_deploy; do
        let CNT=$CNT+1
        [ $CNT -gt 72 ] && post_err_deploy
        let TIME=$CNT*10
        echo Wait file .deployed created. $TIME sec.
        sleep 10
done

sleep 5
echo Set10 server has deployed!!!
sleep 2
exit
'''

    String name
    String description

    Job build(DslFactory dslFactory) {
        dslFactory.job(name) {
            it.description this.description
            logRotator {
                numToKeep 20
            }

            label('master')

            parameters {
                stringParam('IP', '', '')
                stringParam('SHOP_NUMBER', '', '')
            }

            steps {
                copyArtifacts('build_sh_flex') {
                    includePatterns('**/*.sh, *.sh, set10install.sh, **/set10install.sh')
                    buildSelector {
                        latestSuccessful(true)
                    }
                }

                shell(deployLinuxScript)
            }


        }
    }
}
