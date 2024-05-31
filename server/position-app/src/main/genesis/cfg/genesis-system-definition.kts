systemDefinition {
  global {
    item(name = "DbLayer", value = "SQL")
    item(name = "DbHost", value = "jdbc:postgresql://localhost:5432/postgres?user=postgres&password=Password11*")
    item(name = "DEPLOYED_PRODUCT", value = "position")
    item(name = "MqLayer", value = "ZeroMQ")

    item(name = "HookStateStore", value = "DB")
    item(name = "DictionarySource", value = "DB")
    item(name = "AliasSource", value = "DB")
    item(name = "MetricsEnabled", value = "false")
    item(name = "ZeroMQProxyInboundPort", value = "5001")
    item(name = "ZeroMQProxyOutboundPort", value = "5000")
    item(name = "DB_SQL_CONNECTION_POOL_SIZE", value = "3")
    item(name = "DbMode", value = "VANILLA")
    item(name = "GenesisNetProtocol", value = "V2")
    item(name = "ResourcePollerTimeout", value = "5")
    item(name = "ReqRepTimeout", value = "60")
    item(name = "MetadataChronicleMapAverageKeySizeBytes", value = "128")
    item(name = "MetadataChronicleMapAverageValueSizeBytes", value = "1024")
    item(name = "MetadataChronicleMapEntriesCount", value = "512")
    item(name = "DaemonServerPort", value = "4568")
    item(name = "DaemonHealthPort", value = "4569")
    item(
      name = "JVM_OPTIONS",
      value = ""
    )

    item(name = "SYSTEM_DEFAULT_USER_NAME", value = "Genesis System")
    item(name = "SYSTEM_DEFAULT_EMAIL", value = "notifications@genesis.global")
    item(name = "EMAIL_SMTP_HOST", value = "email-smtp.eu-west-2.amazonaws.com")
    item(name = "EMAIL_SMTP_PORT", value = "587")
    item(name = "EMAIL_SMTP_USER", value = "AKIA5Z3A44KXP7A2ZL7C")
    item(name = "EMAIL_SMTP_PW", value = "BEABqs/AWWmxSa1lKiPQIjP3mZ/m0WASMSCQN7xfh51s")
    item(name = "EMAIL_SMTP_PROTOCOL", value = "SMTP_TLS")

    item(name = "ADMIN_PERMISSION_ENTITY_TABLE", value = "COUNTERPARTY")
    item(name = "ADMIN_PERMISSION_ENTITY_FIELD", value = "COUNTERPARTY_ID")
    // item(name = "ClusterMode", value = "CONSUL")
    item(name = "ConsulNodeName", value = "CONSUL_NODE_NAME")
    item(name = "MqttBrokerUrl", value = "MQTT_BROKER_URL")
    item(name = "MqttQos", value = "MQTT_QOS")
    item(name = "DefaultKeystoreLocation", value = "/keystore.jks")
    item(name = "DefaultKeystorePassword", value = "KEY_STORE_PASSWORD")
    item(name = "DefaultCertificate", value = "/certificate.crt")
    item(name = "ConsulServiceNamePattern", value = "PETER_{{PROCESS_NAME}}")
    item(name = "NOTIFY_ENTITY_LIST", value = listOf("COUNTERPARTY", "TRADE_VIEW", "POSITION_VIEW"))
  }

  systems {

    system(name = "DEV") {

      hosts {
        host(LOCAL_HOST)
      }

      item(name = "DbNamespace", value = "position")
      item(name = "ClusterPort", value = "6000")
      item(name = "location", value = "LO")
      item(name = "LogFramework", value = "LOG4J2")
      item(name = "LogFrameworkConfig", value = "log4j2-default.xml")

      // Bloomberg - New Issues
      item(name = "BBG_NEW_ISSUE_SUBSCRIBER_IS_ACTIVE", value = true)
      item(
        name = "BBG_API_ENDPOINT_PRIMARY_HOST_NAME",
        value = "vpce-0a9c484934f2a0ae1-2a485ujy-us-east-1b.vpce-svc-0b48c5e1a15f3314a.us-east-1.vpce.amazonaws.com"
      )
      item(
        name = "BBG_API_ENDPOINT_SECONDARY_HOST_NAME",
        value = "vpce-0a9c484934f2a0ae1-2a485ujy-us-east-1e.vpce-svc-0b48c5e1a15f3314a.us-east-1.vpce.amazonaws.com"
      )
      item(name = "BBG_API_ENDPOINT_APPLICATION_NAME", value = "Genesis:Demo Application")
      item(name = "BBG_API_ENDPOINT_SUBSCRIPTION_SERVICE", value = "//blp/mktdata")
      item(name = "BBG_API_ENDPOINT_AUTHENTICATION_SERVICE", value = "//blp/apiauth")
      item(name = "BBG_API_ENDPOINT_SUBSCRIPTION_TOPIC", value = "/ticker")
      item(name = "PATH_TO_PK12", value = "/home/position/run/site-specific/cfg/certificateName")
      item(name = "PATH_TO_PK7", value = "/home/position/run/site-specific/cfg/rootCertificate.pk7")
    }
  }
}
