package global.genesis.bbg.mktdata.adapter;

import com.bloomberglp.blpapi.*;
import global.genesis.commons.annotation.Module;
import global.genesis.db.EntityModifyDetails;
import global.genesis.db.updatequeue.GenericRecordUpdate;
import global.genesis.gen.dao.*;
import global.genesis.db.rx.entity.multi.RxEntityDb;

import global.genesis.gen.dao.enums.position.market_data_subscription.SubscriptionStatus;
import global.genesis.gen.view.entity.UserView;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

@Module
public class BbgMarketDataSubscription {
  private static final Logger LOG = LoggerFactory.getLogger(BbgMarketDataSubscription.class);
  private static final Name SLOW_CONSUMER_WARNING = Name.getName("SlowConsumerWarning");
  private static final Name SLOW_CONSUMER_WARNING_CLEARED = Name.getName("SlowConsumerWarningCleared");
  private static final Name DATA_LOSS = Name.getName("DataLoss");
  private static final Name SUBSCRIPTION_TERMINATED = Name.getName("SubscriptionTerminated");
  private static final Name SOURCE = Name.getName("source");
  private static final Name AUTHORIZATION_SUCCESS = Name.getName("AuthorizationSuccess");
  private static final Name AUTHORIZATION_FAILURE = Name.getName("AuthorizationFailure");
  private static final Name AUTHORIZATION_REVOKED = Name.getName("AuthorizationRevoked");
  private static final Name TOKEN_SUCCESS = Name.getName("TokenGenerationSuccess");
  private static final Name TOKEN = Name.getName("token");

  private Session d_session;
  private Service d_service;
  private final Map<String, Identity> authenticatedUsersMap;
  private final Map<String, String> usernameEmrsIdMap;
  private final ArrayList<Element> uniqueEids;
  private final ArrayList<String> d_topics;
  private final SubscriptionList d_subscriptions;
  private final SimpleDateFormat d_dateFormat;
  private final String d_service_name;
  private final String d_auth_service_name;

  private String applicationName;
  private String primaryHostname;
  private String secondaryHostname;

  private String UserEMRSId;
  private String UserIpAddress;
  private final String topic;
  private String pathToPk12;
  private String pathToPk7;
  private boolean d_isSlow;
  private boolean d_isStopped;
  private final SubscriptionList d_pendingSubscriptions;
  private final Set<CorrelationID> d_pendingUnsubscribe;
  private final Object d_lock;
  private Identity d_identity;
  private CorrelationID d_authCorrelationId;
  private CorrelationID d_userAuthCorrelationId;
  private static final int FAILED_AUTHORIZATION = -1;
  private static final int NOT_AUTHORIZED = 0;
  private static final int AUTHORIZED = 1;
    private static final String CODE = "LSE";
  private int d_authStatus = NOT_AUTHORIZED;

  private final RxEntityDb rxEntityDb;



  @Inject
  public BbgMarketDataSubscription(@Named("BBG_API_ENDPOINT_PRIMARY_HOST_NAME") String primaryHostname,
                                   @Named("BBG_API_ENDPOINT_SECONDARY_HOST_NAME") String secondaryHostname,
                                   @Named("BBG_API_ENDPOINT_SUBSCRIPTION_SERVICE") String d_service_name,
                                   @Named("BBG_API_ENDPOINT_AUTHENTICATION_SERVICE") String d_auth_service_name,
                                   @Named("BBG_API_ENDPOINT_SUBSCRIPTION_TOPIC") String topic,
                                   @Named("BBG_API_ENDPOINT_APPLICATION_NAME") String applicationName,
                                   @Named("PATH_TO_PK12") String pathToPk12,
                                   @Named("PATH_TO_PK7") String pathToPk7,
                                   RxEntityDb rxEntityDb) {
    this.primaryHostname = primaryHostname;
    this.secondaryHostname = secondaryHostname;
    this.d_service_name = d_service_name;
    this.d_auth_service_name = d_auth_service_name;
    this.topic = topic;
    this.applicationName = applicationName;
    this.pathToPk12 = pathToPk12;
    this.pathToPk7 = pathToPk7;
    this.rxEntityDb = rxEntityDb;

    d_topics = new ArrayList<>();
    authenticatedUsersMap = new HashMap<>();
    usernameEmrsIdMap = new HashMap<>();
    uniqueEids = new ArrayList<>();
    d_subscriptions = new SubscriptionList();
    d_dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
    d_isSlow = false;
    d_isStopped = false;
    d_pendingSubscriptions = new SubscriptionList();
    d_pendingUnsubscribe = new HashSet<>();
    d_lock = new Object();

  }

  @PostConstruct
  private void run() throws Exception {
    registerCallback(Level.OFF);

    // Set the BBG Subscription status to INACTIVE upon startup
    MarketDataSubscription marketDataSubscription = rxEntityDb.get(MarketDataSubscription.bySubscriptionName("BBG")).blockingGet();
    marketDataSubscription.setSubscriptionStatus(SubscriptionStatus.INACTIVE);
    rxEntityDb.upsert(marketDataSubscription).blockingGet();
    LOG.info("Subscription status set to INACTIVE upon startup");

    if (!createSession()) return;
    LOG.info("Back to run() after running createSession()");


    rxEntityDb.subscribe(MarketDataSubscription.class)
      .subscribe(update -> {
        LOG.info("Received a subscription update! " + update);

        this.processSubscriptionUpdate(update);
      });

    rxEntityDb.subscribe(UserLoginAudit.class).subscribe(loginUpdate -> {
      LOG.info("Received a user login audit update! " + loginUpdate);
      this.processLoginAuditUpdate(loginUpdate);
    });
  }

  private void processSubscriptionUpdate(GenericRecordUpdate<MarketDataSubscription> update) throws Exception {
    if (!(update instanceof GenericRecordUpdate.Modify)) return;
    MarketDataSubscription newRecord = ((GenericRecordUpdate.Modify<MarketDataSubscription>) update).getNewRecord();
    MarketDataSubscription oldRecord = ((GenericRecordUpdate.Modify<MarketDataSubscription>) update).getOldRecord();

    if (newRecord.getSubscriptionName().equals("BBG")) {
        if (newRecord.getSubscriptionStatus().equals(SubscriptionStatus.ACTIVE) && oldRecord.getSubscriptionStatus().equals(SubscriptionStatus.INACTIVE)) {

            LOG.info("User Activating Subscription");

            LOG.info("calling authenticateOnlineBBGUsers()");
            authenticateOnlineBBGUsers();


            createSubscriptionDetails();
            LOG.info("createSubscriptionDetails() complete");

            d_session.subscribe(d_subscriptions);
        } else if (newRecord.getSubscriptionStatus().equals(SubscriptionStatus.INACTIVE) && oldRecord.getSubscriptionStatus().equals(SubscriptionStatus.ACTIVE)) {
            LOG.info("User Terminating Subscription");
            d_session.unsubscribe(d_subscriptions);
        } else {
            LOG.info("Subscription Status not changed");
        }
    } else {
        LOG.info("Subscription Change is not for BBG");
    }
  }

  private boolean createSession() throws Exception {
    if (d_session != null) d_session.stop();
    SessionOptions options = new SessionOptions();


    // Server addresses setup
    SessionOptions.ServerAddress[] servers = new SessionOptions.ServerAddress[2];
    servers[0] = new SessionOptions.ServerAddress(primaryHostname, 8194);
    servers[1] = new SessionOptions.ServerAddress(secondaryHostname, 8194);
    options.setServerAddresses(servers);
    String password = "Password11*";  // set password here!!!
    if (password == "")
    {
      LOG.error("Missing ZFP password!");
      return false;
    }
    try {
      TlsOptions tlsOptions = TlsOptions.createFromFiles(pathToPk12, password.toCharArray(), pathToPk7);
      options.setTlsOptions(tlsOptions);
    } catch (TlsOptions.TlsInitializationException ex) {
      LOG.error("Failed to create TlsOptions object: " + ex.getMessage());
      return false;
    }
    AuthOptions authOptions = new AuthOptions(new AuthApplication(applicationName));
    d_authCorrelationId = new CorrelationID("applicationAuthCorrelation");
    options.setSessionIdentityOptions(authOptions, d_authCorrelationId);


    LOG.info("Session options: " + options.toString());
    d_session = new Session(options, new SubscriptionEventHandler(d_topics, d_subscriptions));
    LOG.info("Starting session...\n");
    if (!d_session.start()) {
      LOG.error("Failed to start session\n");
      return false;
    }
    LOG.info("Connected successfully\n");

    if (!d_session.openService(d_service_name)) {
      LOG.error("Failed to open service: %s%n" + d_service_name);
      d_session.stop();
      return false;
    }

    d_service = d_session.getService(d_service_name);

    if (!d_session.openService(d_auth_service_name)) {
      LOG.error("Failed to open service: %s%n" + d_auth_service_name);
      d_session.stop();
      return false;
    }

    LOG.info("Connection status is true, at the end of createSession()\n");
    return true;
  }

  private void authenticateOnlineBBGUsers() throws Exception {
    // 1 - Get all Logged in Users: EMRS ID, and IP Address
    LOG.info("Starting authenticateOnlineBBGUsers() method\n");
    List<UserView> userDetails = rxEntityDb.getBulk(UserView.class).toList().blockingGet();
    List<UserSession> liveUserSessions;


    for (UserView a: userDetails) {
      LOG.info("EvaluatingUser: " + a.getUserName() + " online status is " + a.getOnline() + " and BBG EMRS ID is " + a.getBbgEmrsId());
      if (a.getOnline() && a.getBbgEmrsId() != null) {
        LOG.info("BBG User " + a.getUserName() + " is online");
        UserEMRSId = a.getBbgEmrsId();
        usernameEmrsIdMap.put(a.getBbgEmrsId(), a.getUserName());
        liveUserSessions = rxEntityDb.getRange(UserSession.byUserName(a.getUserName())).toList().blockingGet();
        UserIpAddress = liveUserSessions.get(0).getHost();
        LOG.info("Trying to authenticate User with BBG EMRS ID " + a.getBbgEmrsId() + " and IP Address " + UserIpAddress);
        authenticateUser(UserEMRSId, UserIpAddress);
      }
    }
  }

  private void processLoginAuditUpdate(GenericRecordUpdate<UserLoginAudit> loginUpdate) throws Exception {
    //Listen to the User table for a user moving from offline to online. And authenticate them
    LOG.info("Starting processLoginAuditUpdate() method\n");
    UserLoginAudit loginRecord = ((GenericRecordUpdate.Insert<UserLoginAudit>) loginUpdate).getRecord();

    if (loginRecord.getAuthAction().equals("LOGIN")) {
      LOG.info("User login received for: " + loginRecord.getUserName());

      UserEMRSId = rxEntityDb.get(UserView.byName(loginRecord.getUserName())).blockingGet().getBbgEmrsId();

      if (usernameEmrsIdMap.containsKey(UserEMRSId)) {
        LOG.info("User is already online: " + usernameEmrsIdMap.get(UserEMRSId) + ": do nothing");
      } else {
        usernameEmrsIdMap.put(UserEMRSId, loginRecord.getUserName());
        authenticateUser(UserEMRSId, loginRecord.getIpAddress());
      }
    }
  }

  private void authenticateUser(String UserEMRSId, String UserIpAddress) throws Exception {

    LOG.info("Starting authenticateUser() method\n");

    AuthUser authUser = AuthUser.createWithManualOptions(UserEMRSId, UserIpAddress);
    AuthOptions user_and_app = new AuthOptions(authUser,new AuthApplication(applicationName));

    d_userAuthCorrelationId = new CorrelationID(UserEMRSId);
    d_session.generateAuthorizedIdentity(user_and_app, d_userAuthCorrelationId);

    LOG.info("Called generateAuthorizedIdentity()");

    // Future - new user logs on. Need to run a function to authenticate them and add them to the list

  }


  private void createSubscriptionDetails() throws Exception {
    LOG.info("Starting createSubscriptionDetails() method\n");
    ArrayList<String> fieldList = new ArrayList<String>();
    fieldList.add("BID");
    fieldList.add("ASK");
    fieldList.add("LAST_PRICE");
    fieldList.add("BID_SIZE");
    fieldList.add("ASK_SIZE");
    fieldList.add("LAST_TRADE");
    fieldList.add("EID");
    LOG.info("fieldList: " + fieldList);
    ArrayList<String> subscriptionOptions1 = new ArrayList<String>();
    subscriptionOptions1.add("delayed");
    LOG.info("subscriptionOptions1: " + subscriptionOptions1);

    List<String> instrumentPriceSubscription = new ArrayList<String>();
    List<InstrumentPriceSubscription> instSubscriptions = rxEntityDb.getBulk(InstrumentPriceSubscription.class).toList().blockingGet();

    Maybe<AltInstrumentId> bbgInstrumentRow;
    String bbgInstrumentTicker = "";

    for (InstrumentPriceSubscription a: instSubscriptions) {
      instrumentPriceSubscription.add(a.getInstrumentCode());
      bbgInstrumentRow = rxEntityDb.get(AltInstrumentId.byInstrumentIdAlternateType(a.getInstrumentCode(), "BBG"));
      bbgInstrumentTicker = bbgInstrumentRow.blockingGet().getInstrumentCode();
      LOG.info("new bbgInstrumentTicker: " + bbgInstrumentTicker);
      d_subscriptions.add(new Subscription(bbgInstrumentTicker, fieldList, subscriptionOptions1, new CorrelationID(bbgInstrumentTicker)));
    }
    LOG.info("instrumentPriceSubscription: " + instrumentPriceSubscription);
  }

  // register API logging callback level
  private void registerCallback(Level logLevel) {
    Logging.Callback loggingCallback = new Logging.Callback() {
      @Override
      public void onMessage(long threadId, Level level, Datetime dateTime,
                            String loggerName, String message) {
        LOG.info(dateTime + "  " + loggerName + " [" + level.toString() + "] Thread ID = "
          + threadId + " " + message);
      }
    };

    Logging.registerCallback(loggingCallback, logLevel);
  }

  class SubscriptionEventHandler implements EventHandler {
    ArrayList<String> d_topics;
    SubscriptionList d_subscriptions;

    public SubscriptionEventHandler(ArrayList<String> topics, SubscriptionList subscriptions) {
      d_topics = topics;
      d_subscriptions = subscriptions;
    }

    public void processEvent(Event event, Session session) {
      try {
        switch (event.eventType().intValue()) {
          case Event.EventType.Constants.SUBSCRIPTION_DATA:
            processSubscriptionDataEvent(event, session);
            break;
          case Event.EventType.Constants.SUBSCRIPTION_STATUS:
            synchronized (d_lock) {
              processSubscriptionStatus(event, session);
            }
            break;
          case Event.EventType.Constants.ADMIN:
            synchronized (d_lock) {
              processAdminEvent(event, session);
            }
            break;
          case Event.EventType.Constants.AUTHORIZATION_STATUS:
            synchronized (d_lock) {
              processAuthEvent(event, session);
            }
            break;
          case Event.EventType.Constants.PARTIAL_RESPONSE:
          case Event.EventType.Constants.RESPONSE:
            synchronized (d_lock) {
              processResponseEvent(event, session);
            }
            break;
          default:
            processMiscEvents(event, session);
            break;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    private boolean processSubscriptionStatus(Event event, Session session)
      throws Exception {
      LOG.info("Processing SUBSCRIPTION_STATUS: " + event);
      SubscriptionList subscriptionList = null;
      MessageIterator msgIter = event.messageIterator();
      while (msgIter.hasNext()) {
        Message msg = msgIter.next();
        CorrelationID cid = msg.correlationID();
        String topic = d_topics.get((int) cid.value() - 1);
        LOG.info("processSubscriptionStatus: %s: %s%n" + d_dateFormat.format(Calendar.getInstance().getTime()) + topic);
        LOG.info("MESSAGE: " + msg);

        if (msg.messageType() == SUBSCRIPTION_TERMINATED
          && d_pendingUnsubscribe.remove(cid)) {
          // If this message was due to a previous unsubscribe
          Subscription subscription = getSubscription(cid);
          if (d_isSlow) {
            LOG.info(
              "Deferring subscription for topic = %s because session is slow.%n" + topic);
            d_pendingSubscriptions.add(subscription);
          } else {
            if (subscriptionList == null) {
              subscriptionList = new SubscriptionList();
            }
            subscriptionList.add(subscription);
          }
        }
      }

      if (subscriptionList != null && !d_isStopped) {
        session.subscribe(subscriptionList);
        LOG.info("Trying to subscribe: session.subscribe(subscriptionList)");
      }
      return true;
    }

    private boolean processSubscriptionDataEvent(Event event, Session session)

      //Should we add some logic here to check the EID against our live EID's. If it's new, we check all the users against it?

      throws Exception {
      LOG.info("Processing SUBSCRIPTION_DATA: " + event);
      MessageIterator msgIter = event.messageIterator();

      while (msgIter.hasNext()) {
        Message msg = msgIter.next();
        String instrument_topic = msg.correlationID().object().toString();
        LOG.info("processing a response for topic = " + instrument_topic);
        int numFields = msg.asElement().numElements();
        for (int i = 0; i < numFields; ++i) {
          Element field = msg.asElement().getElement(i);
          if (field.isNull()) {
            LOG.info("processSubscriptionDataEvent: \t\t" + field.name() + " is NULL");
            continue;
          }
          LOG.debug("processSubscriptionDataEvent calling processElement(field) for field = " + field);
          processElement(field, instrument_topic);
        }
      }
      return true;
    }

    private void processElement(Element element, String instrument_topic) throws Exception {
      if (element.isArray()) {
        LOG.debug("processElement1 \t\t" + element.name());
        // process array
        int numOfValues = element.numValues();
        for (int i = 0; i < numOfValues; ++i) {
          // process array data
          processElement(element.getValueAsElement(i), instrument_topic);
        }
      } else if (element.numElements() > 0) {
        LOG.info("processElement2 \t\t" + element.name());
        int numOfElements = element.numElements();
        for (int i = 0; i < numOfElements; ++i) {
          // process child elements
          processElement(element.getElement(i), instrument_topic);
        }
      } else {
        // Assume all values are scalar.
        LOG.debug("Calling populateInstrumentL1Price() for element = " + element.name() + " = " + element.getValueAsString());
        InstrumentL1Price instrumentL1Price = populateInstrumentL1Price(element, instrument_topic);
        try {
          rxEntityDb.upsert(instrumentL1Price).blockingGet();
        } catch (Exception e) {
          LOG.error("Error upserting instrumentL1Price: " + e.getMessage());
        }
      }
    }

    private InstrumentL1Price populateInstrumentL1Price(Element element, String instrument_topic) throws Exception {
      InstrumentL1Price instrumentL1Price = rxEntityDb.get(InstrumentL1Price.instrumentPriceByInstrumentCode(instrument_topic)).blockingGet();
      if (instrumentL1Price == null) {
        LOG.info("Instrument doesn't exist for instrument_topic: " + instrument_topic + "\nCreating new");
        //Create an empty instrumentL1Price object
        instrumentL1Price = new InstrumentL1Price(instrument_topic,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null, null);
      }
      String fieldName = element.name().toString();
      LOG.debug("fieldName: " + fieldName);
      String fieldValue = element.getValueAsString();
      LOG.debug("fieldValue: " + fieldValue);
        switch (fieldName) {
            case "BID" -> instrumentL1Price.setEmsBidPrice(element.getValueAsFloat64());
            case "ASK" -> instrumentL1Price.setEmsAskPrice(element.getValueAsFloat64());
            case "LAST_PRICE" -> instrumentL1Price.setOpenPrice(element.getValueAsFloat64());
            case "BID_SIZE" -> instrumentL1Price.setBidSize(element.getValueAsFloat64());
            case "ASK_SIZE" -> instrumentL1Price.setAskSize(element.getValueAsFloat64());
            case "VWAP" -> instrumentL1Price.setVwap(element.getValueAsFloat64());
            case "LAST_TRADE" -> instrumentL1Price.setLastTrade(element.getValueAsFloat64());
            case "EID" -> manageEids(element, instrumentL1Price);
            default -> LOG.warn("Field with name={} and value={} not recognised", fieldName, fieldValue);
        }
      return instrumentL1Price;
    }

    private InstrumentL1Price manageEids(Element eid, InstrumentL1Price instrumentL1Price) throws Exception {
      Integer eidValue = ((int) eid.getValueAsInt64());
      LOG.info("Procesing the EID: " + eidValue);

      instrumentL1Price.setEid(eidValue);
      if (uniqueEids.contains(eid)) {
        LOG.info("EID already exists");
      } else {
        LOG.info("New EID received, adding to uniqueEids list for permissions check");
        uniqueEids.add(eid);
        //Check permissions for all users against this eid
        checkEidPermissions(eid);
      }
      return instrumentL1Price;
    }

    private void checkEidPermissions(Element eid) throws Exception {
      Integer eidValue = ((int) eid.getValueAsInt64());
      authenticatedUsersMap.forEach((userEmrsId, userIdentity) -> {
        LOG.info("Checking EID permissions for user: " + userEmrsId + " with Identity: " + userIdentity + " for EID: " + eidValue);
        boolean isEntitled = userIdentity.hasEntitlements(eid, d_service);

        if (isEntitled) {
          //Upsert the user entitlement for the EID in the USER_EID table
          String userName = usernameEmrsIdMap.get(userEmrsId);
          LOG.info("Getting username: " + userName + " for EMRS ID: " + userEmrsId);
          UserEid userEid = new UserEid(userName, eidValue);
          try {
            rxEntityDb.upsert(userEid).blockingGet();
            LOG.info("Upserted userEid: " + userEid);
          } catch (Exception e) {
            LOG.error("Error upserting userEid: " + e.getMessage());
          }
        }
      });
    }

    private void checkUserEntitlements(String userEmrsId) throws Exception {
      //A new user has logged in who needs entitlements checked. So we check against all EID's we are getting data for
      LOG.info("Checking user entitlements for the EID's: " + uniqueEids);
      Identity userIdentity = authenticatedUsersMap.get(userEmrsId);
      for (Element eid : uniqueEids) {
        Integer eidValue = ((int) eid.getValueAsInt64());
        LOG.info("Checking EID permissions for user: " + userEmrsId + " with Identity: " + userIdentity + " for EID: " + eidValue);
        boolean isEntitled = userIdentity.hasEntitlements(eid, d_service);

        if (isEntitled) {
          //Upsert the user entitlement for the EID in the USER_EID table
          String userName = usernameEmrsIdMap.get(userEmrsId);
          LOG.info("Getting username: " + userName + " for EMRS ID: " + userEmrsId);
          UserEid userEid = new UserEid(userName, eidValue);
          try {
            rxEntityDb.upsert(userEid).blockingGet();
            LOG.info("Upserted userEid: " + userEid);
          } catch (Exception e) {
            LOG.error("Error upserting userEid: " + e.getMessage());
          }
        }
      }
    }

    private boolean processAuthEvent(Event eventObj, Session session) throws Exception {
      LOG.info("Processing Authorization STATUS: " + eventObj.eventType().toString());
      MessageIterator msgIter = eventObj.messageIterator();
      while (msgIter.hasNext()) {
        Message msg = msgIter.next();
        String cid_topic = (String) msg.correlationID().object();
        LOG.info("processAuthEvent for session with CID: " + cid_topic);
        if (msg.messageType() == AUTHORIZATION_SUCCESS) {
          LOG.info(d_dateFormat.format(Calendar.getInstance().getTime()) + ": Authorization Success for " + cid_topic);
          // use simplified auth credential
          d_authStatus = AUTHORIZED;
          LOG.info("d_authStatus = " + d_authStatus);
          //Need some logic here, if a user is being authenticated. Add them to a new list of authenticatedUsers
          if (d_authCorrelationId.object().toString().equals(cid_topic)) {
            LOG.info("Application: " + cid_topic + " has been authenticated");
          } else {
            LOG.info("User has been authenticated: " + cid_topic + " for application: " + d_authCorrelationId.object());
            // 3 - If successful, add to list of authenticated users. This list will be used for the User/EID checks
            Identity userIdentity = d_session.getAuthorizedIdentity(msg.correlationID());

            //get the authorized identity based on the CID, and store that in the same map. for use in authenticating against EID's
            authenticatedUsersMap.put(cid_topic, userIdentity);
            LOG.info("Checking user entitlements against any registered EID's for " + cid_topic);
            checkUserEntitlements(cid_topic);
          }
        } else if (msg.messageType() == AUTHORIZATION_FAILURE) {
          // authorization failed
          d_authStatus = FAILED_AUTHORIZATION;
          LOG.info(d_dateFormat.format(Calendar.getInstance().getTime()) + ": Authorization Failure for " + cid_topic);
          LOG.info(String.valueOf(msg));
        } else if (msg.messageType() == AUTHORIZATION_REVOKED) {
          // authorization revoked
          LOG.info(d_dateFormat.format(Calendar.getInstance().getTime()) + ": Authorization Revoked");
          LOG.info(String.valueOf(msg));

          // cancel subscriptions
          if (!d_isStopped) {
            d_session.unsubscribe(d_subscriptions);
            d_pendingUnsubscribe.clear();
            LOG.info("Session Stopped" + d_session);
          }
        } else {
          LOG.info(d_dateFormat.format(Calendar.getInstance().getTime()) +
            ": " + msg.messageType());
          LOG.info("What is this" + msg);
        }
        LOG.info("Exiting the while loop in processAuthEvent");
      }
      return true;
    }

    private boolean processResponseEvent(Event eventObj, Session session) throws Exception {
      LOG.info("Processing Authorization STATUS: " + eventObj.eventType().toString());
      MessageIterator msgIter = eventObj.messageIterator();
      while (msgIter.hasNext()) {
        Message msg = msgIter.next();
        String topic = (String) msg.correlationID().object();
        if (msg.messageType() == AUTHORIZATION_SUCCESS
          || msg.messageType() == AUTHORIZATION_FAILURE) {
          // process authorization response
          processAuthEvent(eventObj, session);
          LOG.info("processAuthEvent: " + msg.messageType());
        }
      }
      return true;
    }

    private boolean processAdminEvent(Event event, Session session)
      throws Exception {
      LOG.info("Processing ADMIN: ");
      ArrayList<CorrelationID> cidsToCancel = null;
      boolean previouslySlow = d_isSlow;
      MessageIterator msgIter = event.messageIterator();
      while (msgIter.hasNext()) {
        Message msg = msgIter.next();
        // An admin event can have more than one messages.
        if (msg.messageType() == SLOW_CONSUMER_WARNING) {
          LOG.info("processAdminEvent MESSAGE: %s%n" + msg);
          d_isSlow = true;
        } else if (msg.messageType() == SLOW_CONSUMER_WARNING_CLEARED) {
          LOG.info("processAdminEvent MESSAGE: %s%n" + msg);
          d_isSlow = false;
        } else if (msg.messageType() == DATA_LOSS) {
          CorrelationID cid = msg.correlationID();
          String topic = (String) cid.object();
          LOG.info("processAdminEvent %s: %s%n" + d_dateFormat.format(Calendar.getInstance().getTime()) + topic);
          LOG.info("processAdminEvent MESSAGE: %s%n" + msg);
          if (msg.hasElement(SOURCE)) {
            String sourceStr = msg.getElementAsString(SOURCE);
            if (sourceStr.compareTo("InProc") == 0
              && !d_pendingUnsubscribe.contains(cid)) {
              // DataLoss was generated "InProc". This can only happen if
              // applications are processing events slowly and hence are not
              // able to keep-up with the incoming events.
              if (cidsToCancel == null) {
                cidsToCancel = new ArrayList<CorrelationID>();
              }
              cidsToCancel.add(cid);
              d_pendingUnsubscribe.add(cid);
            }
          }
        }
      }

      if (!d_isStopped) {
        if (cidsToCancel != null) {
          session.cancel(cidsToCancel);
        } else if ((previouslySlow && !d_isSlow) && !d_pendingSubscriptions.isEmpty()) {
          // Session was slow but is no longer slow. subscribe to any topics
          // for which we have previously received SUBSCRIPTION_TERMINATED
          LOG.info("Subscribing to topics - %s%n" + getTopicsString(d_pendingSubscriptions));
          session.subscribe(d_pendingSubscriptions);
          LOG.info("Trying to subscribe: session.subscribe(d_pendingSubscriptions);");
          d_pendingSubscriptions.clear();
        }
      }
      return true;
    }

    private boolean processMiscEvents(Event event, Session session)
      throws Exception {
      LOG.info("processMiscEvents: Processing %s%n" + event.eventType());
      MessageIterator msgIter = event.messageIterator();
      while (msgIter.hasNext()) {
        Message msg = msgIter.next();
        LOG.info("processMiscEvents1 %s: %s%n" + d_dateFormat.format(Calendar.getInstance().getTime()) +msg.messageType());
        LOG.info("processMiscEvents2 %s: %s%n" + d_dateFormat.format(Calendar.getInstance().getTime()) + msg.toString());
      }
      return true;
    }

    private Subscription getSubscription(CorrelationID cid) {
      for (Subscription subscription : d_subscriptions) {
        if (subscription.correlationID().equals(cid)) {
          return subscription;
        }
      }
      throw new IllegalArgumentException(
        "No subscription found corresponding to cid = " + cid.toString());
    }

    private String getTopicsString(SubscriptionList list) {
      StringBuilder strBuilder = new StringBuilder();
      for (int count = 0; count < list.size(); ++count) {
        Subscription subscription = list.get(count);
        if (count != 0) {
          strBuilder.append(", ");
        }
        strBuilder.append((String) subscription.correlationID().object());
      }
      return strBuilder.toString();
    }
  }
}
