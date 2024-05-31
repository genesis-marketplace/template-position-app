dependencies {
  implementation("global.genesis:genesis-process:${properties["genesisVersion"]}")
  implementation("global.genesis:genesis-eventhandler:${properties["genesisVersion"]}")
  implementation("ar.com.fdvs:DynamicJasper:5.3.8")
  implementation("net.sf.jasperreports:jasperreports:6.20.5")
  implementation("com.github.librepdf:openpdf:1.3.30")
  implementation("com.bloomberglp:bloomberglp:${properties["bloomberglp-version"]}")
  api("org.apache.commons:commons-csv:1.10.0")

  testImplementation("junit:junit:${properties["junitVersion"]}")
  testImplementation("org.mockito:mockito-core:${properties["mockitoVersion"]}")
  testApi("org.hamcrest:hamcrest-library:${properties["hamcrestVersion"]}")
  testImplementation("org.awaitility:awaitility:${properties["awaitilityVersion"]}")

  compileOnly(project(path = ":position-dictionary-cache", configuration = "codeGen"))
  testImplementation(project(path = ":position-dictionary-cache", configuration = "codeGen"))

}

description = "position-bbg-adaptor"
