package com.setdsl

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class RestoreVirtualPcStateJobTemplate {
    String name
    String description

    String restoreVmState = '''
    echo ---------- Restore base state $VM_NAME ----------

VM_IP=$(echo $VMS | cut -f1 -d:)
VM_TYPE=$(echo $VMS | cut -f2 -d:)
VM_NAME=$(echo $VMS | cut -f3 -d:)

export FORCE=0

check_vm()
{
        sshpass -p $PASSWORD ssh -o UserKnownHostsFile=/dev/null \
        -o StrictHostKeyChecking=no $USER@$VM_HOST_IP \
        "VBoxManage list runningvms" | grep $VM_NAME && return 0 || return 1
}

check_no_vm()
{
        sshpass -p $PASSWORD ssh -o UserKnownHostsFile=/dev/null \
        -o StrictHostKeyChecking=no $USER@$VM_HOST_IP \
        "VBoxManage list runningvms" | grep $VM_NAME && return 1 || return 0
}

start_vm()
{
        echo Start VM $VM_NAME
        sshpass -p $PASSWORD ssh -o UserKnownHostsFile=/dev/null \
        -o StrictHostKeyChecking=no $USER@$VM_HOST_IP "VBoxManage startvm $VM_NAME -type headless"
}

kickoff_vm()
{
        echo Soft STOP VM $VM_NAME
        if [ $VM_TYPE == linux ]; then
            sshpass -p $PASSWORD ssh -o UserKnownHostsFile=/dev/null \
            -o StrictHostKeyChecking=no root@$VM_IP "shutdown -h 1"
        else
            sshpass -p $PASSWORD ssh -o UserKnownHostsFile=/dev/null \
            -o StrictHostKeyChecking=no $USER@$VM_IP "shutdown /s /t 1"
        fi

}

force_kickoff_vm()
{
        if [ $FORCE == 0 ]; then
            echo Force STOP VM $VM_NAME
            sshpass -p $PASSWORD ssh -o UserKnownHostsFile=/dev/null \
            -o StrictHostKeyChecking=no $USER@$VM_HOST_IP "VBoxManage controlvm $VM_NAME poweroff"
            export FORCE=1
        else
            echo ERROR!!! After force shutdown VM $VM_NAME not stoped!!!
            exit 1
        fi
}

restore_current()
{
        echo Restore $VM_NAME from snapshotà
        sshpass -p $PASSWORD ssh -o UserKnownHostsFile=/dev/null \
        -o StrictHostKeyChecking=no $USER@$VM_HOST_IP "VBoxManage snapshot $VM_NAME restore point"
}

check_ping()
{
        ping -c 1 $VM_IP | grep from && return 1 || return 0
}
check_no_ping()
{
        ping -c 1 $VM_IP | grep from && return 0 || return 1
}
post_err_vm()
{
        echo ERROR!!! After snapshot restore VM $VM_NAME not work
        exit 1
}

check_ping_post()
{
    if check_ping; then
        export VM_ONLINE=0
        echo Connection with VM $VM_NAME on $VM_IP failed.
    else
        export VM_ONLINE=1
        echo Connection with VM $VM_NAME on $VM_IP succes.
    fi
}

check_vm_post()
{
    if check_vm; then
      export VM_ON=1
      echo VM $VM_NAME on $VM_IP is working.
    else
      export VM_ON=0
      echo VM $VM_NAME on $VM_IP is NOT working.
    fi
}
kickoff_vm_post()
{
    if kickoff_vm; then
      echo Soft shutdowning VM $VM_NAME on $VM_IP is succes.
    else
      echo Soft shutdowning VM $VM_NAME on $VM_IP is failed.
    fi
}

check_ping_post
check_vm_post

if [ $VM_ON == 1 ]; then
    kickoff_vm_post
fi

CNT=0
while check_no_ping; do
        let CNT=$CNT+1
        [ $CNT -gt 18 ] && post_err_vm
        let TIME=$CNT*10
        echo Wait disconnect to VM $VM_NAME on $VM_IP. $TIME sec.
        sleep 10
done

CNT=0
while check_vm; do
        let CNT=$CNT+1
        [ $CNT -gt 12 ] && force_kickoff_vm
        let TIME=$CNT*10
        echo Wait shotdown VM $VM_NAME on $VM_IP. $TIME sec.
        sleep 10
done

if restore_current; then
    echo Restoring VM $VM_NAME on $VM_IP is succes.
else
    echo Restoring VM $VM_NAME on $VM_IP is failed.
fi

if start_vm; then
    echo Starting VM $VM_NAME on $VM_IP is succes.
else
    echo Starting VM $VM_NAME on $VM_IP is failed.
fi

CNT=0
while check_no_vm; do
        let CNT=$CNT+1
        [ $CNT -gt 12 ] && post_err_vm
        let TIME=$CNT*10
        echo Wait starting VM $VM_NAME on $VM_IP. $TIME sec.
        sleep 10
done

CNT=0
while check_ping; do
        let CNT=$CNT+1
        [ $CNT -gt 18 ] && post_err_vm
        let TIME=$CNT*10
        echo Wait up link to VM $VM_NAME on $VM_IP. $TIME sec.
        sleep 10
done

sleep 5
echo VM $VM_NAME started!
sleep 2
exit
'''

    Job build(DslFactory dslFactory) {
        dslFactory.job(name) {
            it.description this.description
            logRotator {
                numToKeep 20
            }

            parameters {
                choiceParam('VMS', ['172.20.0.160:linux:standc_server1',
                                    '172.20.0.161:linux:standc_server2',
                                    '172.20.0.140:win:standa_server1',
                                    '172.20.0.141:win:standa_server2'])
            }

            steps {
                environmentVariables {
                    env 'VM_HOST_IP', '172.20.0.34'
                    env 'USER', 'tc'
                    env 'PASSWORD', '324012'
                    env 'VMS', '\$VMS'
                }

                shell(restoreVmState)
            }
        }
    }
}
