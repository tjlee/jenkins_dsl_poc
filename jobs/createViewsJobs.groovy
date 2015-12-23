//import javaposse.jobdsl.dsl.views.jobfilter.Status
//
//listView('Flex') {
//    description('<h3>Flex specific jobs</h3>')
//    filterBuildQueue()
//    filterExecutors()
//    jobs {
//        regex('build_flex.*')
//    }
//    jobFilters {
//        status {
//            status(Status.UNSTABLE)
//        }
//    }
//    columns {
//        status()
//        weather()
//        name()
//        lastSuccess()
//        lastFailure()
//        lastDuration()
//        buildButton()
//    }
//}
//listView('DEV'){
//    description('<h3>Development specific jobs(default client type)</h3>')
//    filterBuildQueue()
//    filterExecutors()
//    jobs {
//        regex('(build_e.*)|(build_i.*)|(build_t.*)')
//    }
//    jobFilters {
//        status {
//            status(Status.UNSTABLE)
//        }
//    }
//    columns {
//        status()
//        weather()
//        name()
//        lastSuccess()
//        lastFailure()
//        lastDuration()
//        buildButton()
//    }
//
//}
//listView('LENTA'){
//    description('<h3>Development specific jobs(default client type)</h3>')
//    filterBuildQueue()
//    filterExecutors()
//    jobs {
//        regex('(build_lenta_.*)')
//    }
//    jobFilters {
//        status {
//            status(Status.UNSTABLE)
//        }
//    }
//    columns {
//        status()
//        weather()
//        name()
//        lastSuccess()
//        lastFailure()
//        lastDuration()
//        buildButton()
//    }
//
//}
//listView('BELARUS'){
//    description('<h3>Belarus development specific jobs(default client type)</h3>')
//    filterBuildQueue()
//    filterExecutors()
//    jobs {
//        regex('(build_belarus_.*)')
//    }
//    jobFilters {
//        status {
//            status(Status.UNSTABLE)
//        }
//    }
//    columns {
//        status()
//        weather()
//        name()
//        lastSuccess()
//        lastFailure()
//        lastDuration()
//        buildButton()
//    }
//
//}
//listView('PULL_REQUESTS'){
//    description('')
//    filterBuildQueue()
//    filterExecutors()
//    jobs {
//        regex('(build_pull.*)')
//    }
//    jobFilters {
//        status {
//            status(Status.UNSTABLE)
//        }
//    }
//    columns {
//        status()
//        weather()
//        name()
//        lastSuccess()
//        lastFailure()
//        lastDuration()
//        buildButton()
//    }
//
//}