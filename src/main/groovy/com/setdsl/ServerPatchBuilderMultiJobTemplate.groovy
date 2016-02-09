package com.setdsl

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job


class ServerPatchBuilderMultiJobTemplate {
    String name
    String description
    Boolean isToRunTests = false

    Job build(DslFactory dslFactory) {
        dslFactory.multiJob(name) {

            wrappers {
                buildName('#${BUILD_NUMBER}.(${ENV,var="VERSION"}-${ENV,var="VERSION_TO"})')
            }

            it.description this.description
            logRotator {
                numToKeep 20
            }

            parameters {
                stringParam('VERSION', '10.2.0.0', '')
                stringParam('BRANCH', 'master', '')
                stringParam('BRANCH_TO', 'master', '')
                stringParam('VERSION_TO', '10.2.0.0', '')
            }

            // run two jobs in phase
            // then run patchbuilder.jar

            steps {
                phase('Build distributions') {
                    phaseJob('build_distr_without_flex') {
                        currentJobParameters(false)
                        parameters {
                            currentBuild()
                            predefinedProp('VERSION', '\$VERSION')
                            predefinedProp('BRANCH', '\$BRANCH')
                            predefinedProp('CUSTOM_WORKSPACE', "\$WORKSPACE/version_from/")
                            sameNode()
                        }
                    }

                    phaseJob('build_distr_flex') {
                        currentJobParameters(false)
                        parameters {
                            currentBuild()
                            predefinedProp('VERSION', '\$VERSION_TO')
                            predefinedProp('BRANCH', '\$BRANCH_TO')
                            predefinedProp('CUSTOM_WORKSPACE', "\$WORKSPACE/version_to/")
                            sameNode()
                        }
                    }
                }

                shell('''mkdir -p \$WORKSPACE/current; cp \$JENKINS_HOME/userContent/PatchBuilder.jar \$WORKSPACE/current;''')

                shell('''mkdir -p \$WORKSPACE/current/builds/\$VERSION;mkdir -p \$WORKSPACE/current/builds/\$VERSION_TO;''')
//                shell('''mkdir -p \$WORKSPACE/current/git/to_git; cp -rf \$WORKSPACE/\$VERSION_TO/patches \$WORKSPACE/current/git/to_git;''')
                shell('''cd \$WORKSPACE/current;java -Dfile.encoding=UTF-8 -jar \$WORKSPACE/current/PatchBuilder.jar gitPathFrom=\$BRANCH gitPathTo=\$BRANCH_TO versionFrom=\$VERSION versionTo=\$VERSION_TO modules=S needTests=true workPath=\$WORKSPACE/current/ disableRebuild=true;''')


                shell('''zip -r \$WORKSPACE/current/patches/\$VERSION_FROM/_\$VERSION_TO.zip .;''')
                shell('''mv \\$WORKSPACE/current/patches/\\$VERSION_FROM/_\\$VERSION_TO.zip \$WORKSPACE;''')

                // todo: build to version_from
                // todo: build to version_to

                // todo:
                // todo:
                // todo:
                // todo:
                // todo:

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

Î_Î_Î_Î_Î_Î_Î_Î_Î_Î

cd $WORKSPACE/current/patches/$VERSION_FROM\_$VERSION_TO
zip -r $VERSION_FROM\_$VERSION_TO.zip .
mv $VERSION_FROM\_$VERSION_TO.zip $WORKSPACE

 */