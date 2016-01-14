Map<String, String> views = ['flex'         : 'build_flex.*',
                             'DEV'          : '(build_e.*)|(build_i.*)|(build_t.*)|(build_pos.*)',
                             'LENTA'        : '(build_lenta_.*)',
                             'BELARUS'      : '(build_belarus_.*)',
                             'PULL_REQUESTS': '(build_pull.*)',
                             'seed'         : '(.*seed)']


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
        cron('@hourly')
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


