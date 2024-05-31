package global.genesis.position.message.event


import global.genesis.gen.dao.enums.position.trade.Side
import global.genesis.gen.dao.enums.position.trade.TradeMatchStatus
import global.genesis.gen.dao.enums.position.trade.TradeStatus
import global.genesis.message.core.annotation.LongMax
import global.genesis.message.core.annotation.LongMin

data class TradeModify(
  val tradeId: String,
  val instrumentId: String,
  val counterpartyId: String,
  @LongMin(1)
  @LongMax(100000)
  val quantity: Long,
  val side: Side,
  val price: Double,
  val tradeDatetime: String,
  val enteredBy: String,
  val tradeStatus: TradeStatus,
  val tradeMatchStatus: TradeMatchStatus
)
