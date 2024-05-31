package global.genesis.position.message.event
data class GenerateTradeDocumentRequest(
  val templateId: String
)

data class LinkDocumentTemplateAssetsRequest(
  val templateId: String,
  val assetIds: List<String>
)

data class EmailGeneratorData(
  val header: String,
  val body: String,
  val sender: String,
  val pdfTemplateId: String,
  val tradeId: String,
  val fileName: String,
  val emailTemplateRef: String,
  val tableEntityId: String,
  val tableName: String,
  val topic: String
)
