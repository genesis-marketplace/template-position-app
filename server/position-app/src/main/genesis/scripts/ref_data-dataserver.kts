package scripts
/**
 * System              : Genesis Business Library
 * Sub-System          : ref_data_app Configuration
 * Version             : 1.0
 * Copyright           : (c) Genesis
 * Date                : 06 September 2021
 * Function : Provide dataserver config for ref_data_app.
 *
 * Modification History
 */
dataServer {

    query("ALL_ALT_COUNTERPARTY_IDS", ALT_COUNTERPARTY_ID)
    query("ALL_ALT_INSTRUMENT_IDS", ALT_INSTRUMENT_ID)
    query("ALL_COUNTERPARTYS", COUNTERPARTY)
    query("ALL_INSTRUMENTS", INSTRUMENT)
    // Add a subscription based on INSTRUMENT_L1_PRICE_VIEW that only shows rows with ALTERNATE_TYPE=BBG
    query("ALL_BBG_INSTRUMENT_SUBSCRIPTION", ALT_INSTRUMENT_L1_PRICE_VIEW) {
      permissioning {
        permissionCodes = listOf("VIEW_BBG_DATA")
        auth(mapName = "EID_VISIBILITY") {
          ALT_INSTRUMENT_L1_PRICE_VIEW.EID
        }
      }
      where { it.alternateType == "BBG" }
    }
    query("ALL_RIC_INSTRUMENT_SUBSCRIPTION", ALT_INSTRUMENT_L1_PRICE_VIEW) {
      where { it.alternateType == "RIC" }
    }
}
