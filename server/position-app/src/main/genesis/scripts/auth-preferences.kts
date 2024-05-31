package scripts

security {
    heartbeatIntervalSecs = 30
    sessionTimeoutMins = 500
    expiryCheckMins = 10
    maxSimultaneousUserLogins = 0

    authentication {
        ssoToken()
        genesisPassword {
            retry {
                maxAttempts = 3
                waitTimeMins = 5
            }
        }
    }
}
