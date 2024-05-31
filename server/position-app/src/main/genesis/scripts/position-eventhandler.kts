import global.genesis.TradeStateMachine
import global.genesis.commons.standards.GenesisPaths
import global.genesis.gen.view.repository.TradeViewAsyncRepository
import global.genesis.jackson.core.GenesisJacksonMapper
import global.genesis.message.core.event.ApprovalType
import global.genesis.position.message.event.TradeInsert
import global.genesis.position.message.event.TradeModify
import global.genesis.position.message.event.TradeCancel
import global.genesis.position.message.event.TradeAllocated
import global.genesis.position.message.event.InstrumentComplex
import global.genesis.position.message.event.PositionReport
import global.genesis.position.message.event.EmailGeneratorData
import global.genesis.position.message.event.GenerateTradeDocumentRequest
import global.genesis.position.message.event.LinkDocumentTemplateAssetsRequest
import global.genesis.file.templates.DocumentGenerator
import global.genesis.file.templates.DocumentStorageConfiguration
import global.genesis.gen.dao.enums.position.trade.TradeStatus
import global.genesis.gen.dao.enums.position.trade.Side


import java.io.File
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat


/**
 *
 *   System              : position
 *   Sub-System          : position Configuration
 *   Version             : 1.0
 *   Copyright           : (c) GENESIS
 *   Date                : 2021-09-07
 *
 *   Function : Provide Event Handler configuration for position.
 *
 *   Modification History
 *
 */
val tradeViewRepo = inject<TradeViewAsyncRepository>()

eventHandler {
  val stateMachine = inject<TradeStateMachine>()
  val documentGenerator = inject<DocumentGenerator>()

  eventHandler<TradeInsert>(name = "TRADE_INSERT", transactional = true) {
    permissioning {
      permissionCodes = listOf("TRADER")
      auth(mapName = "ENTITY_VISIBILITY") {
        field { counterpartyId }
      }
    }
    onValidate { event ->
      val message = event.details
      verify {
        entityDb hasEntry Counterparty.ById(message.counterpartyId)
        entityDb hasEntry Instrument.ById(message.instrumentId)
      }
      ack()
    }
    onCommit { event ->
      val trade = event.details
      stateMachine.insert(entityDb, Trade {
        instrumentId = trade.instrumentId
        counterpartyId = trade.counterpartyId
        quantity = trade.quantity
        side = Side.valueOf(trade.side.toString())
        price = trade.price
        tradeDatetime = parseDateTime(trade.tradeDatetime)
        tradeDate = parseDateToStartOfDay(trade.tradeDate)
        enteredBy = trade.enteredBy.ifBlank { event.userName }
        tradeStatus = trade.tradeStatus
        tradeMatchStatus = trade.tradeMatchStatus
      })
      entityDb.upsert(InstrumentPriceSubscription {
        instrumentCode = trade.instrumentId
        primaryExchangeId = "LSE"
      })
      ack()
    }
  }

  //When name is not specified, the name of the event will be derived from the meta class name -  EVENT_TRADE_CANCEL
  eventHandler<TradeCancel>(transactional = true) {
    onCommit { event ->
      val message = event.details
      stateMachine.modify(entityDb, message.tradeId) { trade ->
        trade.tradeStatus = TradeStatus.CANCELLED
      }
      ack()
    }
  }
  eventHandler<TradeAllocated>(transactional = true) {
    onCommit { event ->
      val message = event.details
      stateMachine.modify(entityDb, message.tradeId) { trade ->
        trade.tradeStatus = TradeStatus.ALLOCATED
      }
      ack()
    }
  }

  eventHandler<TradeModify>(name = "TRADE_MODIFY", transactional = true) {
    permissioning {
      permissionCodes = listOf("TRADER")
      auth(mapName = "ENTITY_VISIBILITY") {
        field { counterpartyId }
      }
    }
    onCommit { event ->
      val trade = event.details
      stateMachine.modify(entityDb, trade.tradeId) {
        it.tradeId = trade.tradeId
        it.instrumentId = trade.instrumentId
        it.counterpartyId = trade.counterpartyId
        it.quantity = trade.quantity
        it.side = Side.valueOf(trade.side.toString())
        it.price = trade.price
        it.tradeDatetime = parseDateTime(trade.tradeDatetime)
        it.enteredBy = trade.enteredBy
        it.tradeStatus = trade.tradeStatus
      }
      ack()
    }
  }

  eventHandler<PositionReport> {
    onCommit {
      val mapper = GenesisJacksonMapper.csvWriter<TradeView>()
      val today = LocalDate.now().toString()
      val positionReportFolder = File(GenesisPaths.runtime()).resolve("position-daily-report")
      if (!positionReportFolder.exists()) positionReportFolder.mkdirs()

      tradeViewRepo.getBulk()
        .toList()
        .groupBy { it.counterpartyName }
        .forEach { (counterParty, trades) ->
          val file = positionReportFolder.resolve("${counterParty}_$today.csv")
          if (file.exists()) file.delete()
          mapper.writeValues(file).use { it.writeAll(trades) }
        }

      ack()
    }
  }

  eventHandler<MarketDataSubscription>(name = "MANAGE_MARKET_DATA_SUBSCRIPTION", transactional = true) {
    onCommit { event ->
      val marketDataSubscription = event.details
      entityDb.modify(marketDataSubscription)
      ack()
    }
  }

  eventHandler<InstrumentComplex>(name = "INSTRUMENT_INSERT_SUBSCRIPTION", transactional = true) {
    onCommit { event ->
      val req = event.details
      entityDb.insert(
        Instrument {
          instrumentId = req.instrumentId
          name = req.name
          marketId = "1"
          countryCode = req.countryCode
          currencyId = "1"
          assetClass = "EQ"
        }
      )
      entityDb.insert(
        InstrumentPriceSubscription {
          instrumentCode = req.instrumentId
          primaryExchangeId = "LSE"
        }
      )
      entityDb.insert(
        InstrumentLimits {
          instrumentId = req.instrumentId
          instrumentLimit = req.instrumentLimit
        }
      )
      ack()
    }
  }

  eventHandler<InstrumentLimits>(name = "UPDATE_INSTRUMENT_LIMITS") {
    requiresPendingApproval { event ->
      event.userName != "system.user"
      event.approvalMessage = "My Custom Approval Message"
      true
    }
    onValidate { event ->
      val req = event.details

      approvableAck(
        entityDetails = listOf(
          ApprovalEntityDetails(
            entityTable = "INSTRUMENT_LIMITS",
            entityId = event.details.instrumentId,
            approvalType = ApprovalType.UPDATE
          )
        ),
        approvalMessage = "Limits update for ${event.details.instrumentId} has been sent for approval.",
        approvalType = ApprovalType.UPDATE,
        additionalDetails = "Testing details"
      )
    }
    onCommit { event ->
      val req = event.details
      entityDb.modify(req)
      ack()
    }
  }

  eventHandler<GenerateTradeDocumentRequest>(name = "CREATE_DAILY_TRADE_REPORT") {
    onCommit { event ->
      val details = event.details
      val todaysTrades = entityDb.getBulk(TRADE).toList().filter{ it.tradeDate == now().withTimeAtStartOfDay() }

      val contextMap : MutableMap<String, Any> = mutableMapOf(
        "trades" to todaysTrades
      )

      val config = DocumentStorageConfiguration(
        templateId = details.templateId,
        fileName = "trades.pdf",
        userName = event.userName,
        data = contextMap,
        deleteOnExit = true,
      )
      documentGenerator.generateAndStore(config)
      ack()
    }
  }

  eventHandler<EmailGeneratorData>(name = "GENERATE_EMAIL") {
    onValidate {
      ack()
    }

    onCommit { event ->
      val details = event.details
      val allTrades = entityDb.getBulk(TRADE).toList()

      val contextMap : MutableMap<String, Any> = mutableMapOf(
        "trades" to allTrades
      )

      val config = DocumentStorageConfiguration(
        templateId = details.pdfTemplateId,
        fileName = "trades.pdf",
        userName = event.userName,
        data = contextMap,
        deleteOnExit = true,
      )
      val pdf = documentGenerator.generateAndStore(config)

      val notify = Notify {
        this.body = details.body
        this.header = details.header
        this.notifySeverity = NotifySeverity.Information
        this.tableEntityId = details.tableEntityId
        this.tableName = details.tableName
        this.templateRef = details.emailTemplateRef
        this.topic = details.topic
        this.documentId = pdf.fileStorageId
      }

      val messageClient = serviceDiscovery.resolveClientByResource("EVENT_NOTIFY_INSERT")
      messageClient!!.sendMessage(
        Event (
          details = notify,
          messageType = "EVENT_NOTIFY_INSERT",
          userName = event.userName
        )
      )
      ack()
    }
  }
}

fun parseDateTime(dateTimeString: String): DateTime {
  return try {
    DateTime.parse(dateTimeString)
  } catch (e: Exception) {
    now()
  }
}

fun parseDateToStartOfDay(dateTimeString: String?): DateTime {
  return try {
    val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
    val dateTime = DateTime.parse(dateTimeString, formatter)
    dateTime.withTime(0, 0, 0, 0) // Set time to start of the day (00:00:00)
  } catch (e: Exception) {
    now().withTimeAtStartOfDay()
  }
}






