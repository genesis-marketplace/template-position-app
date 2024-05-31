import global.genesis.gen.config.fields.Fields

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

views {

  view("TRADE_VIEW", TRADE) {

    joins {
      joining(COUNTERPARTY) {
        on(TRADE.COUNTERPARTY_ID to COUNTERPARTY { COUNTERPARTY_ID })
      }
      joining(INSTRUMENT) {
        on(TRADE.INSTRUMENT_ID to INSTRUMENT { INSTRUMENT_ID })
      }
    }

    fields {
      TRADE.allFields()

      COUNTERPARTY.NAME withPrefix COUNTERPARTY
      INSTRUMENT.NAME withPrefix INSTRUMENT
      INSTRUMENT.CURRENCY_ID withAlias "CURRENCY"

      derivedField("CONSIDERATION", DOUBLE) {
        // I: F2*H2
        withInput(TRADE.QUANTITY, TRADE.PRICE) { QUANTITY, PRICE ->
          QUANTITY * PRICE
        }
      }

      derivedField("OVERALL_STATUS", STRING) {
        withInput(TRADE.TRADE_STATUS, TRADE.TRADE_MATCH_STATUS)
        { status, matchStatus ->
          if (TradeStatus.NEW == status) {
            "UNALLOCATED"
          } else if (TradeStatus.ALLOCATED == status) {
            if (TradeMatchStatus.UNMATCHED == matchStatus) {
              "ALLOCATED_UNMATCHED"
            } else {
              "ALLOCATED_MATCHED"
            }
          } else {
            null
          }
        }
      }
    }
  }

  view("POSITION_VIEW", POSITION) {

    val refinitive_id = ALT_INSTRUMENT_ID withAlias "refinitive_id"
    val refinitive_l1_price = INSTRUMENT_L1_PRICE withAlias "refinitive_l1_price"

    joins {
      joining(refinitive_id, backwardsJoin = true) {
        on(POSITION.INSTRUMENT_ID to refinitive_id { INSTRUMENT_ID })
          .and(refinitive_id { ALTERNATE_TYPE } to "REFINITIV")

          .joining(refinitive_l1_price, backwardsJoin = true) {
            on(refinitive_id { INSTRUMENT_CODE } to refinitive_l1_price { INSTRUMENT_CODE })
          }
      }

      joining(INSTRUMENT) {
        on(POSITION.INSTRUMENT_ID to INSTRUMENT { INSTRUMENT_ID })
      }
    }

    fields {
      POSITION.allFields()

      INSTRUMENT.NAME withPrefix INSTRUMENT
      INSTRUMENT.CURRENCY_ID withAlias "CURRENCY"

      refinitive_l1_price { EMS_BID_PRICE withAlias "REFINITIV_LAST_PX" }

      derivedField("OPENING_VALUE", DOUBLE) {
        withInput(POSITION.NET_QUANTITY, POSITION.NET_AVG_PX) { NET_AVG_PX, NET_QUANTITY ->
          NET_QUANTITY * NET_AVG_PX
        }
      }

      derivedField("CURRENT_VALUE", DOUBLE) {
        withInput(POSITION.NET_QUANTITY, refinitive_l1_price {EMS_BID_PRICE}) {NET_QUANTITY, EMS_BID_PRICE ->
          NET_QUANTITY * EMS_BID_PRICE
        }
      }

      derivedField( "PNL", DOUBLE) {
        withInput(POSITION.NET_QUANTITY, POSITION.NET_AVG_PX, refinitive_l1_price{EMS_BID_PRICE} ) { NET_QUANTITY, NET_AVG_PX, EMS_BID_PRICE ->
          NET_QUANTITY * (EMS_BID_PRICE - NET_AVG_PX)
        }
      }
    }
  }

  view("TRADE_DAILY_VIEW",TRADE_DAILY) {

    joins {
      joining(INSTRUMENT) {
        on(TRADE_DAILY.INSTRUMENT_ID to INSTRUMENT { INSTRUMENT_ID })
      }
    }

    fields {
      TRADE_DAILY.allFields()

      INSTRUMENT.NAME withPrefix INSTRUMENT
      INSTRUMENT.CURRENCY_ID withAlias "CURRENCY"
    }
  }

  view("INSTRUMENT_PRICE_HISTORY_VIEW",INSTRUMENT_PRICE_HISTORY) {

    joins {
      joining(INSTRUMENT) {
        on(INSTRUMENT_PRICE_HISTORY.INSTRUMENT_ID to INSTRUMENT { INSTRUMENT_ID })
      }
    }

    fields {
      INSTRUMENT_PRICE_HISTORY.allFields()
      INSTRUMENT.NAME withPrefix INSTRUMENT
      INSTRUMENT.CURRENCY_ID withAlias "CURRENCY"
    }
  }

  view("INSTRUMENT_SUBSCRIPTION_VIEW", INSTRUMENT) {
    val refinitive_id = ALT_INSTRUMENT_ID withAlias "refinitive_id"
    val bloomberg_id = ALT_INSTRUMENT_ID withAlias "bloomberg_id"

    joins {
      joining(refinitive_id, backwardsJoin = true) {
        on(INSTRUMENT.INSTRUMENT_ID to refinitive_id { INSTRUMENT_ID })
          .and(refinitive_id { ALTERNATE_TYPE } to "REFINITIV")

        }

      joining(bloomberg_id, backwardsJoin = true) {
        on(INSTRUMENT.INSTRUMENT_ID to bloomberg_id { INSTRUMENT_ID })
          .and(bloomberg_id { ALTERNATE_TYPE } to "BBG")

        }

      joining(INSTRUMENT_LIMITS, backwardsJoin = true) {
        on(INSTRUMENT.INSTRUMENT_ID to INSTRUMENT_LIMITS { INSTRUMENT_ID })
      }
    }

    fields {
      INSTRUMENT.allFields()

      refinitive_id { INSTRUMENT_CODE withAlias "REFINITIV_INSTRUMENT_ID" }
      bloomberg_id { INSTRUMENT_CODE withAlias "BBG_INSTRUMENT_ID" }

      INSTRUMENT_LIMITS { INSTRUMENT_LIMIT }
    }
  }

  view("ALT_INSTRUMENT_L1_PRICE_VIEW", ALT_INSTRUMENT_ID) {
    joins {
      joining(INSTRUMENT_L1_PRICE, backwardsJoin = true) {
        on(ALT_INSTRUMENT_ID { INSTRUMENT_CODE } to INSTRUMENT_L1_PRICE { INSTRUMENT_CODE })
      }
    }
    fields {
      ALT_INSTRUMENT_ID.allFields()
      INSTRUMENT_L1_PRICE {
        EID
        EMS_BID_PRICE
        EMS_ASK_PRICE
        LOW_PRICE
        HIGH_PRICE
        OPEN_PRICE
        BID_SIZE
        ASK_SIZE
        VWAP
        LAST_TRADE
      }
    }
  }
}
