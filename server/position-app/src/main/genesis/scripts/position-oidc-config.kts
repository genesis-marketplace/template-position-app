package scripts
import global.genesis.auth.oidc.script.NewUserStrategy

oidc{
  loginEndpoint = "https://dev-genesis-demo-position2.cddev.genesis.global/login"
  identityProvider("genesis"){
    client{
      id = "6612535f-984c-4d1c-9fb1-f508abee61df"
      secret = "gnQ8Q~e3t2DL0tDoBF6bmDF_-I_4LWFwaEKiDaWN"
    }

    config {
      endpoints{
        token = "https://login.microsoftonline.com/5f4c432a-2ed3-44db-a2ab-ffb3b97da200/oauth2/v2.0/token"
        authorization = "https://login.microsoftonline.com/5f4c432a-2ed3-44db-a2ab-ffb3b97da200/oauth2/v2.0/authorize"
      }
    }

    tokenLifeInSeconds = 5000

    onNewUser = NewUserStrategy.ONLY_UPDATE_USER

    redirectUri = "https://dev-genesis-demo-position2.cddev.genesis.global/gwf/oidc/logon"
  }
}
