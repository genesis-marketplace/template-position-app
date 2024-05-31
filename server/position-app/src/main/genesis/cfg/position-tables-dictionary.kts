/**
 *
 *   System              : position
 *   Sub-System          : position Configuration
 *   Version             : 1.0
 *   Copyright           : (c) GENESIS
 *   Date                : 2021-09-07
 *
 *   Function : Provide Dictionary configuration for position.
 *
 *   Modification History
 *
 */

tables {
  table (name = "TRADE", id = 11000, audit = details(id = 11003, sequence = "TR", tsKey = true)) {
    field("TRADE_ID", STRING).primaryKey().sequence("TR")
    field("REMOTE_TRADE_ID", STRING)
    field("INSTRUMENT_ID", STRING).notNull()
    field("COUNTERPARTY_ID", STRING).notNull()
    field("QUANTITY", LONG).notNull()
    field("SIDE", ENUM("BUY", "SELL"))
    field("PRICE", DOUBLE).notNull()
    field("TRADE_DATETIME", DATETIME)
    field("ENTERED_BY", STRING)
    field("TRADE_STATUS", ENUM("NEW", "ALLOCATED", "CANCELLED"))
    field("TRADE_MATCH_STATUS", ENUM("UNMATCHED", "MATCHED"))
    field("TRADE_DATE", DATE)
  }


  table (name = "TRADE_DAILY", id = 1107) {
    field("TRADE_DATE", DATE).notNull()
    field("INSTRUMENT_ID", STRING).notNull()
    field("TOTAL_PRICE", DOUBLE).notNull()
    field("AVG_PRICE", DOUBLE).notNull()
    field("NO_OF_TRADES", INT).notNull()
    field("DAILY_TRADE_QUANTITY", LONG).notNull()

    primaryKey("TRADE_DATE", "INSTRUMENT_ID")
  }

  table(name= "POSITION", id = 11001) {
    field("POSITION_ID", STRING).primaryKey().sequence("PS")
    field("INSTRUMENT_ID", STRING).uniqueIndex()
    field("BUY_QUANTITY", LONG).notNull()
    field("SELL_QUANTITY", LONG).notNull()
    field("NET_QUANTITY", LONG)
    field("TOTAL_QUANTITY", LONG)
    field("TOTAL_CONSIDERATION", DOUBLE)
    field("BUY_CONSIDERATION", DOUBLE)
    field("SELL_CONSIDERATION", DOUBLE)
    field("BUY_AVG_PX", DOUBLE)
    field("SELL_AVG_PX", DOUBLE)
    field("NET_AVG_PX", DOUBLE)
  }

  table(name = "COMPANY", id = 11002) {
    field("COMPANY_NAME", STRING).primaryKey()
    field("COMPANY_LOCATION", STRING)
  }

  table(name = "TRADE_LOOKUP", id = 11006) {

    field("REMOTE_TRADE_ID", STRING).primaryKey().notNull()
    field("TRADE_ID", STRING).uniqueIndex()

  }

  table(name = "INSTRUMENT_PRICE_HISTORY", id = 11008) {
    field("INSTRUMENT_ID", STRING)
    field("MARKET_DATE", DATE)
    field("OPEN_PRICE", DOUBLE)
    field("CLOSE_PRICE", DOUBLE)
    field("HIGH_PRICE", DOUBLE)
    field("LOW_PRICE", DOUBLE)

    primaryKey("INSTRUMENT_ID", "MARKET_DATE")
  }

  table(name = "MARKET_DATA_SUBSCRIPTION", id = 11009) {
    field("SUBSCRIPTION_NAME", STRING).primaryKey()
    field("SUBSCRIPTION_STATUS", ENUM("INACTIVE", "ACTIVE"))
  }

  table(name = "INSTRUMENT_LIMITS", id = 11010) {
    field("INSTRUMENT_ID", STRING).primaryKey()
    field("INSTRUMENT_LIMIT", DOUBLE)
  }

  tableUnion(name = "INSTRUMENT_L1_PRICE") {
    field("EID", INT)
  }

  tableUnion(name = "USER_ATTRIBUTES") {
    field("BBG_EMRS_ID", STRING)
  }

  table(name = "USER_EID", id = 11011) {
    field("USER_NAME", STRING)
    field("EID", INT)

    primaryKey("USER_NAME", "EID")
  }
}
