package com.setdsl

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job


class POC_PROP_FILE {
    String name
    String description

    Job build(DslFactory dslFactory) {
        dslFactory.job(name) {
            it.description this.description


        parameters {
            stringParam('TEST_SOURCE_BRANCH', 'master', '')
        }

            environmentVariables {
                // todo: later on copy to job
               propertiesFile '\$JENKINS_HOME/jobs/seed/workspace/resources/stand_c.properties'
            }

            steps {

                shell('echo \$CENTRUM_IP')
            }

    }
    }
}
//                            propertiesFile 'stand_c.properties'
