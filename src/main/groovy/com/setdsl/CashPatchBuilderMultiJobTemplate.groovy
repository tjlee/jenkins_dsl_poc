package com.setdsl

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job


class CashPatchBuilderMultiJobTemplate {
    String name
    String description
    Boolean isToRunTests = false
    String artifacts = '*.zip'


    Job build(DslFactory dslFactory) {
        dslFactory.multiJob(name) {

//            wrappers {
//                buildName('#${BUILD_NUMBER}.(${ENV,var="VERSION"}-${ENV,var="VERSION_TO"})')
//            }

            it.description this.description
            logRotator {
                numToKeep 20
            }

            label('master')

            parameters {
                stringParam('VERSION', '10.2.0.0', '')
                stringParam('BRANCH', 'master', '')
                stringParam('VERSION_TO', '10.2.0.0', '')
                stringParam('BRANCH_TO', 'master', '')

            }

            steps {
                phase('Build distributions') {
                    phaseJob('build_pos_cash_tar_custom_workspace') {
                        currentJobParameters(false)
                        parameters {
                            currentBuild()
                            predefinedProp('VERSION', '\$VERSION')
                            predefinedProp('BRANCH', '\$BRANCH')
                            predefinedProp('CUSTOM_WORKSPACE', '/var/lib/jenkins/workspace/build_cash_patch/builds/\$VERSION/cash/')
                            sameNode()
                        }
                    }

                    phaseJob('build_pos_cash_tar_custom_workspace') {
                        currentJobParameters(false)
                        parameters {
                            currentBuild()
                            predefinedProp('VERSION', '\$VERSION_TO')
                            predefinedProp('BRANCH', '\$BRANCH_TO')
                            predefinedProp('CUSTOM_WORKSPACE', '/var/lib/jenkins/workspace/build_cash_patch/builds/\$VERSION_TO/cash/')
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
                // /var/lib/jenkins/workspace/delete_me

                shell('''mkdir -p \$WORKSPACE/git/to_git; cp -rf \$WORKSPACE/builds/\$VERSION_TO/cash/setretail10/patches \$WORKSPACE/git/to_git''')

                // pos , lenta , belarus in each untar 	crystal-cash.tar // crystal-conf.tar
                // todo: remove hardcode?
                shell('rm -rf \$WORKSPACE/builds/\$VERSION_TO/cash/Belarus;rm -rf \$WORKSPACE/builds/\$VERSION_TO/cash/Lenta;')
                shell('rm -rf \$WORKSPACE/builds/\$VERSION/cash/Belarus;rm -rf \$WORKSPACE/builds/\$VERSION/cash/Lenta;')

                shell('tar -xvf \$WORKSPACE/builds/\$VERSION_TO/cash/POS/crystal-cash.tar -C \$WORKSPACE/builds/\$VERSION_TO/cash/POS/;' +
                        'tar -xvf \$WORKSPACE/builds/\$VERSION_TO/cash/POS/crystal-conf.tar -C \$WORKSPACE/builds/\$VERSION_TO/cash/POS/;' +
                        'rm -f \$WORKSPACE/builds/\$VERSION_TO/cash/POS/crystal-*.tar;')

                shell('tar -xvf \$WORKSPACE/builds/\$VERSION/cash/POS/crystal-cash.tar -C \$WORKSPACE/builds/\$VERSION/cash/POS/;' +
                        'tar -xvf \$WORKSPACE/builds/\$VERSION/cash/POS/crystal-conf.tar -C \$WORKSPACE/builds/\$VERSION/cash/POS/;' +
                        'rm -f \$WORKSPACE/builds/\$VERSION/cash/POS/crystal-*.tar;')

                shell('''java -Dfile.encoding=UTF-8 -jar \$JENKINS_HOME/userContent/PatchBuilder.jar  gitPathFrom=\$BRANCH gitPathTo=\$BRANCH_TO versionFrom=\$VERSION versionTo=\$VERSION_TO modules=C needTests=true workPath=\$WORKSPACE/ disableRebuild=true;''')
                shell('''cd "\$WORKSPACE/patches/\$VERSION"_"\$VERSION_TO" && zip -r "pos_\$VERSION"_"\$VERSION_TO.zip" "pos"_"\$VERSION"_"\$VERSION_TO/";''')
            shell('''mv  "\$WORKSPACE/patches/\$VERSION"_"\$VERSION_TO/pos_\$VERSION"_"\$VERSION_TO.zip" \$WORKSPACE;''')

            }

            publishers {
                archiveArtifacts {
                    pattern(this.artifacts)
                    onlyIfSuccessful()
                }
            }
        }
    }
}

