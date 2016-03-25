package com.setdsl

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job


/*
* Piece of shit*/

class ServerExeJobTemplate {
    String name
    String description

    String gitHubCredentials = "4e269209-1b8f-4f0b-a849-c7376ed088e0"
    String gitHubOwnerAndProject = "crystalservice/setretail10"
    String gitHubCheckoutDir = "setretail10"


    String buildType = "exe"/* iso, tgz, exe, ear, distr, sh */
    String clientType /*default lenta belarus*//*for default leave empty*/

    /// so need to make mapping default / lenta / belarus
    /// if default => leave empty kinda shit :)


    Boolean isPullRequest = false


    Boolean isToPublishUnitTests = true
    Boolean isToPublishJaCoCo = true


    Boolean isToDeploy = false // is it here ? or maybe implement in another and make build flow &*&(**?

    Boolean isCustomWorkspace = false
    String customWorkspacePath = ''

    Boolean isToBuildByPush = false

    String artifacts = 'FLEX.war,*.branch,*.ear,*.sh,*.tgz,*.iso, *.exe'

    List<String> emails = ['v.chernov@crystals.ru']

    Job build(DslFactory dslFactory) {
        dslFactory.job(name) {
            it.description this.description
            logRotator {
                artifactDaysToKeep(14)
                numToKeep 28
            }

            label('windows_test')

            if (this.isToBuildByPush) {
                triggers {
                    githubPush()
                }
            }

            parameters {
                stringParam('VERSION', '10.2.0.0', '')
                stringParam('BRANCH', 'master', '')

                if (this.buildType) {
                    booleanParam('WILD_FLY', false, '')
                }
            }


            multiscm {

                // build branch/tag (default isPullRequest= false)
                if (!this.isPullRequest) {

                    git {
                        remote {
                            github(this.gitHubOwnerAndProject)
                            credentials(this.gitHubCredentials)
                            branch('\$BRANCH')
                            refspec('+refs/heads/*:refs/remotes/origin/*')
                        }

                        cloneTimeout 20
                        relativeTargetDir(this.gitHubCheckoutDir)
//                        wipeOutWorkspace true
                    }
                }
                // build pull request
                else {
                    git {
                        remote {
                            github(this.gitHubOwnerAndProject)
                            credentials(this.gitHubCredentials)
                            branch('remotes/origin/pr/\$BRANCH/merge')
                            refspec('+refs/pull/*:refs/remotes/origin/pr/*')
                        }

                        cloneTimeout 20
                        relativeTargetDir(this.gitHubCheckoutDir)
//                        wipeOutWorkspace true
                    }
                }

            }


            steps {

                // here we need to take last successful from build flex

                copyArtifacts('build_flex') {
                    includePatterns('FLEX.war')
                    buildSelector {
                        latestSuccessful(true)
                    }
                }

//                    shell('cd "\$WORKSPACE/' + this.gitHubCheckoutDir + '/SetRetail10_Server/buildGradle"; touch FLEX.war;')


                if (this.buildType) {
                    // copy gradle wrapper
                    shell('''
                        mkdir -p "\$WORKSPACE/gradle/wrapper";
                        cp \$JENKINS_HOME/userContent/wrapper/* \$WORKSPACE/gradle/wrapper || true;
                        cp \$JENKINS_HOME/userContent/gradlew \$WORKSPACE/gradlew || true;
                        cp \$JENKINS_HOME/userContent/gradlew.bat \$WORKSPACE/gradlew.bat || true;
                    ''')

                    if (this.buildType == "exe") {
                        shell('''
                        mkdir -p "\$WORKSPACE/gradle/wrapper";
                        cp /c/gradle-wrapper/gradle/wrapper/* \$WORKSPACE/gradle/wrapper || true;
                        cp /c/gradle-wrapper/gradlew.bat \$WORKSPACE/gradlew.bat || true;
                    ''')

                        gradle('makeWinInstaller',
                                '-PtempDir=/c/Temp -PmoduleVersion=\$VERSION -PdistrDir=\$WORKSPACE -Pbranch=\$BRANCH -Pshaid=\$GIT_COMMIT -PuseEmu -Ptest -PwildFly=\$WILD_FLY' +
                                        (this.clientType ? ' -PclientId=' + this.clientType : ''),
                                true) {
                            it / rootBuildScriptDir('\$WORKSPACE/' + this.gitHubCheckoutDir + '/SetRetail10_Server/Installation')
                            it / wrapperScript('gradlew')
                            it / makeExecutable(true)
                            it / fromRootBuildScriptDir(false)
                        }

                        // copying with bash.exe on windows
                        shell('''
                    cd "\$WORKSPACE";
                    for file in *.exe
                    do
                    if [[ "$file" == *"setup.exe"* ]]
                    then
                    mv -fv "$file" Set"\$VERSION"''' + (this.clientType ? '_' + this.clientType : '') + '''_setup.exe
                    fi
                    done''')
                    }

                    shell('mv -f "\$WORKSPACE/' + this.gitHubCheckoutDir + '/SetRetail10_Server/buildGradle/build/libs/Set10.ear" "\$WORKSPACE"')
                    shell('cd "\$WORKSPACE/' + this.gitHubCheckoutDir + '/SetRetail10_Server/buildGradle"; rm ./*.war -f;')


                }
            }

            // todo: copy to aftifacts server

            publishers {

                if (this.isToPublishUnitTests) {
                    archiveJunit('**/test-results/*.xml')
                }

                if (this.isToPublishJaCoCo) {
                    jacocoCodeCoverage {
                        execPattern('**/**.exec')
                        classPattern('**/classes')
                        sourcePattern('**/src/main/java')
                        exclusionPattern('**/*ContextFactory$1*, **/*ContextFactory*,*/Aladdin/**,**/hasp/**,*/org/**, **/vo/**,**/xmlview/**,**/xmlstubs/**,**/jestery/**,**/supermag/**,**/xml/**,**/enums/**,**/visualization/**,**/exceptions/**,**/exception/**,**/stubs/**,**/newstub/**,**/test/**')
                    }
                }


                if (this.emails) {
//                    mailer this.emails.join(' ')
                    extendedEmail('\$DEFAULT_RECIPIENTS', '\$DEFAULT_SUBJECT', '\$DEFAULT_CONTENT') {
                        trigger(triggerName: 'FirstFailure', sendToDevelopers: true, sendToRequester: true, includeCulprits: true, sendToRecipientList: false)
                    }
                }

                archiveArtifacts {
                    pattern(this.artifacts)
                    onlyIfSuccessful()
                }

            }

        }


    }
}
