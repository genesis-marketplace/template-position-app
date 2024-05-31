import global.genesis.gen.config.fields.Fields
import global.genesis.gen.config.tables.POSITION
import global.genesis.gen.config.tables.POSITION.BUY_QUANTITY
import global.genesis.gen.config.tables.POSITION.NET_QUANTITY
import global.genesis.gen.config.tables.POSITION.SELL_QUANTITY
import global.genesis.gen.config.tables.POSITION.TOTAL_QUANTITY
import global.genesis.gen.config.tables.POSITION.TOTAL_CONSIDERATION
import global.genesis.gen.config.tables.POSITION.BUY_CONSIDERATION
import global.genesis.gen.config.tables.POSITION.SELL_CONSIDERATION
import global.genesis.gen.config.tables.TRADE_DAILY.DAILY_TRADE_QUANTITY
import global.genesis.gen.config.tables.TRADE_DAILY.NO_OF_TRADES
import global.genesis.gen.config.tables.TRADE_DAILY.TOTAL_PRICE
import global.genesis.gen.config.view.TRADE_VIEW
import global.genesis.gen.dao.Position
import global.genesis.gen.dao.enums.position.trade.Side
import global.genesis.gen.dao.enums.position.trade.TradeStatus

consolidators {
  consolidator("CONSOLIDATE_POSITIONS", TRADE_VIEW, POSITION) {
    select {

      // Define your consolidated fields simple language. Supports sum, average, count and other functions

      sum { quantity } onlyIf { side == Side.BUY && tradeStatus != TradeStatus.CANCELLED } into BUY_QUANTITY

      sum { quantity } onlyIf { side == Side.SELL && tradeStatus != TradeStatus.CANCELLED } into SELL_QUANTITY

      sum { quantity } onlyIf { tradeStatus != TradeStatus.CANCELLED } into TOTAL_QUANTITY

      sum { quantity * price } onlyIf { tradeStatus != TradeStatus.CANCELLED } into TOTAL_CONSIDERATION

      sum { quantity * price } onlyIf { side == Side.BUY && tradeStatus != TradeStatus.CANCELLED } into BUY_CONSIDERATION

      sum { quantity * price } onlyIf { side == Side.SELL && tradeStatus != TradeStatus.CANCELLED } into SELL_CONSIDERATION
    }

    // Leverage the output of some consolidated values to do a secondary calculation using onCommit
    onCommit {
      output.buyAvgPx = output.buyConsideration / output.buyQuantity
      output.sellAvgPx = output.sellConsideration / output.sellQuantity
      output.netAvgPx = output.totalConsideration / output.totalQuantity
      output.netQuantity = output.buyQuantity - output.sellQuantity
    }

    // Choose the field to group / consolidate by
    groupBy {
      instrumentId
    } into {
      lookup {
        Position.ByInstrumentId(groupId)
      }
      build {
        Position {
          instrumentId = groupId
          netQuantity = 0
          buyQuantity = 0
          sellQuantity = 0
          totalQuantity = 0
          totalConsideration = 0.0
          buyConsideration = 0.0
          sellConsideration = 0.0
          netAvgPx = 0.0
          buyAvgPx = 0.0
          sellAvgPx = 0.0
        }
      }
    }
  }
  consolidator("CONSOLIDATE_TRADE_DAILY", TRADE_VIEW, TRADE_DAILY) {
    select {
      sum { quantity } onlyIf { tradeStatus != TradeStatus.CANCELLED } into DAILY_TRADE_QUANTITY
      count { quantity } into NO_OF_TRADES
      sum { price } into TOTAL_PRICE
    }
    onCommit {
      output.avgPrice = output.totalPrice / output.noOfTrades
    }
    groupBy { TradeDaily.byTradeDateInstrumentId(tradeDate!!, instrumentId)} into {
      build {
        TradeDaily {
          tradeDate = groupId.tradeDate
          instrumentId = groupId.instrumentId
          totalPrice = 0.0
          dailyTradeQuantity = 0
          avgPrice = 0.0
          noOfTrades = 0
        }
      }
    }
  }
}
