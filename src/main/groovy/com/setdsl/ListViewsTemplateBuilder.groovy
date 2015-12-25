package com.setdsl

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class ListViewsTemplateBuilder {
    String name
    String description
    Map<String, String> views = [:]


    Job build(DslFactory dslFactory) {
        dslFactory.multiJob(name) {
            it.description this.description
            logRotator {
                numToKeep 50
            }

            steps {

                if (this.views) {

                    this.views.each { k, v ->

                        listView(k) {
                            description('')
                            filterBuildQueue()
                            filterExecutors()
                            jobs {
                                regex(v)
                            }

                            columns {
                                status()
                                weather()
                                name()
                                lastSuccess()
                                lastFailure()
                                lastDuration()
                                buildButton()
                            }
                        }
                    }
                }
            }
        }
    }
}

