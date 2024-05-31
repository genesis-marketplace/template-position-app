package global.genesis.position.message.event

data class InstrumentComplex(
  val instrumentId: String,
  val name: String,
  val countryCode: String,
  val instrumentCode: String,
  val instrumentLimit: Double,
)
