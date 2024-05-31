saml {
  strictMode = false
  debugMode = true
  // this should be the URL of the application logon screen
  loginEndpoint = "https://dev-genesis-demo-position2.cddev.genesis.global/login"
  tokenLifeInSeconds = 3000

  serviceProvider {
    // this should be the url for accessing the router
    entityId = "https://dev-genesis-demo-position2.cddev.genesis.global/gwf"
  }

  // for every identity provider we support we need one of these
  identityProvider("genesis") {
    // we need the IDP metadata, either a file:
    metadataUrl = "genesismetadata.xml"

    // where do we get the email address from
    mapToAttribute("email")
  }
}
