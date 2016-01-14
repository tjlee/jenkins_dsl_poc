job('seed') {
    scm {
        github 'tjlee/jenkins_dsl_poc'
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
        dsl {
            external 'jobs/**/*Jobs.groovy'
            additionalClasspath 'src/main/groovy'
        }
    }
}
