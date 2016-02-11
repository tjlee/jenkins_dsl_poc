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


                shell('cp -rf \$WORKSPACE/builds/\$VERSION_TO/cash/patches \$WORKSPACE/git/to_git')


//                shell('''mkdir -p \$WORKSPACE/current/builds/\$VERSION;mkdir -p \$WORKSPACE/current/builds/\$VERSION_TO;''')

//                shell('''mkdir -p \$WORKSPACE/version_to/patches; cp -rf \$WORKSPACE/\$VERSION_TO/patches \$WORKSPACE/current/git/to_git;''')
//
                shell('''java -Dfile.encoding=UTF-8 -jar \$JENKINS_HOME/userContent/PatchBuilder.jar  gitPathFrom=\$BRANCH gitPathTo=\$BRANCH_TO versionFrom=\$VERSION versionTo=\$VERSION_TO modules=C needTests=true workPath=\$WORKSPACE/ disableRebuild=true;''')
//
                shell('''zip -r \$WORKSPACE/patches/\$VERSION_FROM/_\$VERSION_TO.zip .;''')
//                shell('''mv \$WORKSPACE/current/patches/\$VERSION_FROM/_\$VERSION_TO.zip \$WORKSPACE;''')

            }
        }
    }
}
/*
Build name #${BUILD_NUMBER}.(${ENV,var="VERSION_FROM"}-${ENV,var="VERSION_TO"})

// todo: wtf?
cd $WORKSPACE/current/
mkdir git
cd git
mkdir to_git
cd $WORKSPACE
/bin/cp -f -r $WORKSPACE/version_to/patches $WORKSPACE/current/git/to_git


cd $WORKSPACE/current/
java -Dfile.encoding=UTF-8 -jar PatchBuilder.jar gitPathFrom=$GIT_PATH_FROM gitPathTo=$GIT_PATH_TO versionFrom=$VERSION_FROM versionTo=$VERSION_TO modules=$MODULES needTests=$NEED_TESTS workPath=$WORKSPACE/current/ disableRebuild=true

�_�_�_�_�_�_�_�_�_�

cd $WORKSPACE/current/patches/$VERSION_FROM\_$VERSION_TO
zip -r $VERSION_FROM\_$VERSION_TO.zip .
mv $VERSION_FROM\_$VERSION_TO.zip $WORKSPACE

 */