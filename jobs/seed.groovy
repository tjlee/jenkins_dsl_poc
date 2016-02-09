job('seed') {
    scm {
        github 'tjlee/jenkins_dsl_poc'
    }
    triggers {
        cron('@daily')
    }
    steps {

        shell('''mkdir -p \$JENKINS_HOME/userContent/wrapper;
        cp \$WORKSPACE/gradle/wrapper/* \$JENKINS_HOME/userContent/wrapper|| true;
        cp \$WORKSPACE/gradle/wrapper/gradlew \$JENKINS_HOME/userContent/wrapper/gradlew || true;
        cp \$WORKSPACE/gradle/wrapper/gradlew.bat \$JENKINS_HOME/userContent/wrapper/gradlew.bat || true;''')

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
