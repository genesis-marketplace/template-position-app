import global.genesis.gradle.plugin.simple.ProjectType

rootProject.name = "genesisproduct-position"

pluginManagement {
    pluginManagement {
        val genesisVersion: String by settings

        plugins {
            id("global.genesis.settings") version genesisVersion
        }
    }

    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://genesisglobal.jfrog.io/genesisglobal/dev-repo")
            credentials {
                username = extra.properties["genesisArtifactoryUser"].toString()
                password = extra.properties["genesisArtifactoryPassword"].toString()
            }
            content {
                fun disableIfTrue(
                    property: String,
                    moduleRegex: String,
                ) {
                    if (extra.properties[property] == "true") excludeModuleByRegex("global.genesis", moduleRegex)
                }

                disableIfTrue("localGenesis", "genesis-(?!crowley)[\\w-]+")
                disableIfTrue("localAuth", "auth-[\\w-]+")
                disableIfTrue("localFix", "fix-[\\w-]+")
                disableIfTrue("localMarketData", "market-data-[\\w-]+")
                disableIfTrue("localElektron", "elektron-[\\w-]+")
                disableIfTrue("localRefData", "ref_data_app-[\\w-]+")
                disableIfTrue("localDeployPlugin", "deploy-gradle-plugin")
                disableIfTrue("localCrowley", "genesis-crowley-[\\w-]+")
            }
        }
        mavenLocal {
            // VERY IMPORTANT!!! EXCLUDE AGRONA AS IT IS A POM DEPENDENCY AND DOES NOT PLAY NICELY WITH MAVEN LOCAL!
            content {
                excludeGroup("org.agrona")
            }
        }
    }
}

plugins {
    id("global.genesis.settings")
}

genesis {
    projectType = ProjectType.APPLICATION

    dependencies {
        dependency("global.genesis:file-server:${extra.properties["file-serverVersion"]}")
        dependency("global.genesis:genesis-notify:${extra.properties["genesis-notifyVersion"]}")
        dependency("global.genesis:auth:${extra.properties["authVersion"]}")
        dependency("global.genesis:market-data:${extra.properties["marketDataVersion"]}")
        dependency("global.genesis:elektron:${extra.properties["elektronVersion"]}")
        dependency("global.genesis:ref_data:${extra.properties["refDataVersion"]}")
        dependency("global.genesis:reporting:${extra.properties["reportingVersion"]}")
    }

    plugins {
        genesisDeploy.enabled = true
    }

}


include("position-app")
include("position-bbg-adaptor")

