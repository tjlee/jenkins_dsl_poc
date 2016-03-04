package com.setdsl

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job


class ServerPatchBuilderMultiJobTemplate {
    String name
    String description
    Boolean isToRunTests = false

    Job build(DslFactory dslFactory) {
        dslFactory.multiJob(name) {

//            wrappers {
//                buildName('#${BUILD_NUMBER}.(${ENV,var="VERSION"}-${ENV,var="VERSION_TO"})')
//            }

            it.description this.description
            logRotator {
                numToKeep 20
            }

            parameters {
                stringParam('VERSION', '10.2.0.0', '')
                stringParam('BRANCH', 'master', '')
                stringParam('VERSION_TO', '10.2.0.0', '')
                stringParam('BRANCH_TO', 'master', '')

            }

            steps {
                phase('Build distributions without flex') {
                    phaseJob('build_distr_without_flex') {
                        currentJobParameters(false)
                        parameters {
                            currentBuild()
                            predefinedProp('VERSION', '\$VERSION')
                            predefinedProp('BRANCH', '\$BRANCH')
                            predefinedProp('CUSTOM_WORKSPACE', '/var/lib/jenkins/jobs/build_server_patch/workspace/builds/\$VERSION/server/')
                            sameNode()
                        }
                    }
                }

                phase('Build distributions with flex'){
                    phaseJob('build_distr_flex') {
                        currentJobParameters(false)
                        parameters {
                            currentBuild()
                            predefinedProp('VERSION', '\$VERSION_TO')
                            predefinedProp('BRANCH', '\$BRANCH_TO')
                            predefinedProp('CUSTOM_WORKSPACE', '/var/lib/jenkins/jobs/build_server_patch/workspace/builds/\$VERSION_TO/server/')
                            sameNode()
                        }
                    }
                }

                shell('cp -rf \$WORKSPACE/builds/\$VERSION_TO/server/patches \$WORKSPACE/git/to_git')
                shell('''java -Dfile.encoding=UTF-8 -jar \$JENKINS_HOME/userContent/PatchBuilder.jar gitPathFrom=\$BRANCH gitPathTo=\$BRANCH_TO versionFrom=\$VERSION versionTo=\$VERSION_TO modules=S needTests=true workPath=\$WORKSPACE/ disableRebuild=true;''')
                shell('''cd "\$WORKSPACE/patches/\$VERSION_FROM"_"\$VERSION_TO" && zip -r "pos_\$VERSION_FROM"_"\$VERSION_TO.zip" "pos"_"\$VERSION_FROM"_"\$VERSION_TO/";''')
//                shell('''zip -r \$WORKSPACE/patches/\$VERSION_FROM/_\$VERSION_TO.zip .;''')
//                shell('''mv \$WORKSPACE/current/patches/\$VERSION_FROM/_\$VERSION_TO.zip \$WORKSPACE;''')

            }
        }
    }
}
