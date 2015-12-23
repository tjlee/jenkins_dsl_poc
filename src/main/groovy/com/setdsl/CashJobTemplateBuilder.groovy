package com.setdsl

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

/**
 * Build Cash Job Template
 */
class CashJobTemplateBuilder {
    String name
    String description

    String gitHubCredentials = "31df12ac-5d1f-495d-99fe-ad351505d316"

    String gitHubOwnerAndProject = "crystalservice/setretail10"
    String gitHubCheckoutDir = "setretail10"

    String gitHubOwnerAndProjectLinuxSources = "crystalservice/setretail10linux"
    String gitHubCheckoutDirLinuxSources = "setretail10linux"

    String buildType /* tar or iso*/
    String clientType /*default lenta belarus*/ /*POS, Lenta, Belarus*/

    Boolean isPullRequest = false
    Boolean isToPublishUnitTests = true

    String artifacts = 'POS/**/*,Lenta/**/*,Belarus/**/*,cash.branch,*.iso,*.zip'

    List<String> emails = []

    Job build(DslFactory dslFactory) {
        dslFactory.job(name) {
            it.description this.description
            logRotator {
                numToKeep 50
            }


            parameters {
                stringParam('VERSION', '10.2.0.0', '')
                stringParam('BRANCH', 'master', '')
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
                        wipeOutWorkspace true
                    }
                }
                // build pull request
                else {
                    git {
                        remote {
                            github(this.gitHubOwnerAndProject)
                            credentials(this.gitHubCredentials)
                            branch('remotes/origin/pr/\$PULL_NUMBER/merge')
                            refspec('+refs/pull/*:refs/remotes/origin/pr/*')
                        }

                        cloneTimeout 20
                        relativeTargetDir(this.gitHubCheckoutDir)
                        wipeOutWorkspace true
                    }
                }
                // if iso need to checkout linux repo
                if (this.buildType == "iso") {
                    git {
                        remote {
                            github(this.gitHubOwnerAndProjectLinuxSources)
                            credentials(this.gitHubCredentials)
                            branch('master')
                            refspec('+refs/heads/*:refs/remotes/origin/*')
                        }

                        cloneTimeout 20
                        relativeTargetDir(this.gitHubCheckoutDirLinuxSources)
                        wipeOutWorkspace true

                    }
                }
            }


            steps {

                shell('cd' + this.gitHubCheckoutDir + '/SetRetail10_Cash/buildGradle/; export LANG=ru_RU.UTF-8;')

                gradle('clean build tarAll',
                        ' -PpresetCashParamsPath=\$JENKINS_HOME/userContent/cashes.xml -PproductVersion=\$VERSION -xtest -Pbranch=\$GIT_BRANCH -Pshaid=\$GIT_COMMIT',
                        true) {
                    it / wrapperScript('gradlew')
                }

                shell('cp -f -r -a \$WORKSPACE/' + this.gitHubCheckoutDir + '/SetRetail10_Cash/buildGradle/build/distributions/{POS,Lenta,Belarus} \$WORKSPACE/')
                shell('cp -f -r -a \$WORKSPACE/' + this.gitHubCheckoutDir + '/SetRetail10_Cash/buildGradle/build/LentaConfigs/cash-configs.tar.gz \$WORKSPACE/Lenta')

                if (this.buildType == "iso") {

                    shell('cd \$WORKSPACE/' + this.gitHubCheckoutDirLinuxSources + '/cash-tinycore3/;')

                    if (this.clientType == 'pos') {

                        shell('./build.sh -i=set-retail-\$VERSION-pos.iso -d=\$WORKSPACE -s=\$WORKSPACE/POS/crystal-cash.tar -c=\$WORKSPACE/POS/crystal-conf.tar -z=yes -v=java7')

                    } else if (this.clientType == 'lenta') {

                        shell('./build.sh -i=set-retail-$VERSION-lenta.iso -d=$WORKSPACE -s=$WORKSPACE/Lenta/crystal-cash.tar -c=$WORKSPACE/Lenta/crystal-conf.tar -z=yes -v=java7 -l=$WORKSPACE/Lenta/cash-configs.tar.gz')

                    } else if (this.clientType == 'belarus') {

                        shell(' ./build.sh -i=set-retail-$VERSION-belarus.iso -d=$WORKSPACE -s=$WORKSPACE/Belarus/crystal-cash.tar -c=$WORKSPACE/Belarus/crystal-conf.tar -z=yes -v=java7')

                    }
                }

            }

            publishers {
                if (this.isToPublishUnitTests) {
                    archiveJunit('**/test-results/*.xml')
                }

                if (this.emails) {
                    mailer this.emails.join(' ')
                }

                archiveArtifacts {
                    pattern(this.artifacts)
                    onlyIfSuccessful()
                }

            }
        }
    }
}