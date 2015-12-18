package com.setdsl

import javaposse.jobdsl.dsl.views.jobfilter.Status

listView('Flex') {
    description('All Flex jobs')
    filterBuildQueue()
    filterExecutors()
    jobs {
        regex('build_flex')
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

