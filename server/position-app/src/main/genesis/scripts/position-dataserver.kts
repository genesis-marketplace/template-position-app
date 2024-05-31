/**
 *
 *   System              : position
 *   Sub-System          : position Configuration
 *   Version             : 1.0
 *   Copyright           : (c) GENESIS
 *   Date                : 2021-09-07
 *
 *   Function : Provide Data Server Configuration for position.
 *
 *   Modification History
 *
 */

dataServer {

  query("ALL_TRADES", TRADE_VIEW) {
    permissioning {
      permissionCodes = listOf("TRADER", "SUPPORT")
    }
    indices {
      unique {
        TRADE_DATETIME
        TRADE_ID
      }
    }
  }

  query("ALL_TRADES_AUDIT", TRADE_AUDIT) {
    permissioning {
      permissionCodes = listOf("TRADER", "SUPPORT")
    }
  }

  query("ALL_POSITIONS", POSITION_VIEW) {
    config {
      backwardsJoin = true
    }
    permissioning {
      permissionCodes = listOf("TRADER", "SUPPORT")
    }
  }

  query("ALL_TRADE_DAILY",TRADE_DAILY_VIEW) {
    indices {
      unique {
        INSTRUMENT_ID
        TRADE_DATE
      }
    }
  }

  query("ALL_INSTRUMENT_PRICE_HISTORY", INSTRUMENT_PRICE_HISTORY_VIEW) {
    indices {
      unique {
        INSTRUMENT_ID
        MARKET_DATE
      }
    }
  }

  query("ALL_INSTRUMENT_SUBSCRIPTION", INSTRUMENT_SUBSCRIPTION_VIEW)
  query("ALL_MARKET_DATA_SUBSCRIPTION", MARKET_DATA_SUBSCRIPTION)

}
