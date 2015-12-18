job('seed') {
    scm {
        github 'tjlee/jenkins_dsl_poc'
    }
    steps {
        gradle 'clean test'
        dsl {
            external 'jobs/**/*Jobs.groovy'
            additionalClasspath 'src/main/groovy'
        }
    }
}
// todo: make view seed job!!!
/*
*
* listView('Flex') {
    description('All Flex jobs')
    filterBuildQueue()
    filterExecutors()
    jobs {
		names('flex_linux', 'flex_linux_unit')
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
* */