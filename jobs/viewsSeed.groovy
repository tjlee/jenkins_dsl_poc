Map<String, String> views = ['flex': 'build_flex.*', 'DEV': '(build_e.*)|(build_i.*)|(build_t.*)', 'LENTA': '(build_lenta_.*)', 'BELARUS': '(build_belarus_.*)', 'PULL_REQUESTS': '(build_pull.*)', 'seed': '(.*seed)']


String dslText = ''

views.each { k, v ->

    dslText += '''listView({$k}) {
                            description('')
                            filterBuildQueue()
                            filterExecutors()
                            jobs {
                                regex({$v})
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
                        }'''
}

job('views_seed') {

    scm {
        git {
            remote {
                github 'tjlee/jenkins_dsl_poc'
//                credentials '31df12ac-5d1f-495d-99fe-ad351505d316'
            }

            wipeOutWorkspace true
        }
    }

    steps {
        gradle 'clean test'
        dsl {
            if (views) {

                dsl {
                    text(dslText)
                }


            }
        }
    }
}


