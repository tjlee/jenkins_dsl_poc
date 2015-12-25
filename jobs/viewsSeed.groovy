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
            external 'autoqa/jenkins_dsl/jobs/**/*Views.groovy'
            additionalClasspath 'src/main/groovy'
        }
    }
}

