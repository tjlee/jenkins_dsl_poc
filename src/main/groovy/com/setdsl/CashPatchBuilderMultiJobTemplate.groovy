package com.setdsl

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job


class CashPatchBuilderMultiJobTemplate {
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
                phase('Build distributions') {
                    phaseJob('build_pos_cash_tar') {
                        currentJobParameters(false)
                        parameters {
                            currentBuild()
                            predefinedProp('VERSION', '\$VERSION')
                            predefinedProp('BRANCH', '\$BRANCH')
                            predefinedProp('CUSTOM_WORKSPACE', '/var/lib/jenkins/jobs/build_cash_patch/workspace/builds/\$VERSION/cash/')
                            sameNode()
                        }
                    }

                    phaseJob('build_pos_cash_tar') {
                        currentJobParameters(false)
                        parameters {
                            currentBuild()
                            predefinedProp('VERSION', '\$VERSION_TO')
                            predefinedProp('BRANCH', '\$BRANCH_TO')
                            predefinedProp('CUSTOM_WORKSPACE', '/var/lib/jenkins/jobs/build_cash_patch/workspace/builds/\$VERSION_TO/cash/')
                            sameNode()
                        }
                    }
                }

                // ./

                // ./builds/%version_from%/cash
                // ./builds/%version_from%/server

                // ./builds/%version_to%/cash
                // ./builds/%version_to%/server

                // ./git/from_git
                // ./git/to_git

                // ./patches


                shell('''mkdir -p \$WORKSPACE/git/to_git; cp -rf \$WORKSPACE/builds/\$VERSION_TO/cash/setretail10/patches \$WORKSPACE/git/to_git''')

                // pos , lenta , belarus in each untar 	crystal-cash.tar // crystal-conf.tar
                // todo: remove hardcode?
                shell('tar -xvf \$WORKSPACE/builds/\$VERSION_TO/cash/POS/crystal-cash.tar -C \$WORKSPACE/builds/\$VERSION_TO/cash/POS/;tar -xvf \$WORKSPACE/builds/\$VERSION_TO/cash/POS/crystal-conf.tar -C \$WORKSPACE/builds/\$VERSION_TO/cash/POS/;rm -f \$WORKSPACE/builds/\$VERSION_TO/cash/POS/crystal-*.tar;')
                shell('tar -xvf \$WORKSPACE/builds/\$VERSION_TO/cash/POS/crystal-cash.tar -C \$WORKSPACE/builds/\$VERSION/cash/POS/;tar -xvf \$WORKSPACE/builds/\$VERSION/cash/POS/crystal-conf.tar -C \$WORKSPACE/builds/\$VERSION/cash/POS/;rm -f \$WORKSPACE/builds/\$VERSION/cash/POS/crystal-*.tar;')

                shell('''java -Dfile.encoding=UTF-8 -jar \$JENKINS_HOME/userContent/PatchBuilder.jar  gitPathFrom=\$BRANCH gitPathTo=\$BRANCH_TO versionFrom=\$VERSION versionTo=\$VERSION_TO modules=C needTests=true workPath=\$WORKSPACE/ disableRebuild=true;''')
                shell('''zip -r \$WORKSPACE/patches/\$VERSION_FROM/_\$VERSION_TO.zip .;''')
//                shell('''mv \$WORKSPACE/current/patches/\$VERSION_FROM/_\$VERSION_TO.zip \$WORKSPACE;''')

            }
        }
    }
}

