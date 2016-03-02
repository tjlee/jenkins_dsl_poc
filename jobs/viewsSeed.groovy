Map<String, String> views = ['FLEX'  : 'build_flex.*',
                             'SERVER': '(build_e.*)|(build_i.*)|(build_t.*)|(build_sh.*)|(build_pull_request_e.*)|(build_pull_request_i.*)|(build_pull_request_sh.*)',
                             'POS'   : '(build_pull_request.*_cash.*)|(build_pos.*)',
                             /*'LENTA'        : '(build_lenta_.*)',
                             'BELARUS'      : '(build_belarus_.*)',
                             'PULL_REQUESTS': '(build_pull.*)',*/
                             'PATCH' : '.*_patch',
                             'TEST'  : '(run_.*)|(rus_.*)',
                             'SEED'  : '(.*seed)',
                             'DEPLOY': '(deploy.*)']


String dslText = ''

views.each { k, v ->

    dslText += String.format(
            '''
listView('%s') {
    description('')
    filterBuildQueue()
    filterExecutors()
    jobs {
        regex('%s')
    }

    jobFilters {
        status {
            status(Status.UNSTABLE)
        }
    }

    columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
    }
}
''', k, v)
}

job('views_seed') {
    scm {
        git {
            remote {
                github 'tjlee/jenkins_dsl_poc'
            }

            wipeOutWorkspace true
        }
    }

    triggers {
        cron('@daily')
    }

    steps {
        gradle('clean test', '', true)
                {
                    it / wrapperScript('gradlew')
                    it / makeExecutable(true)
                }

        if (views) {
            dsl {
                text(dslText)
            }
        }
    }
}


