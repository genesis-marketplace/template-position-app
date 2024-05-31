# Application Overview

This project has been created from the Genesis Positions Application Seed. Our seeds allow users to quickly bootstrap
their projects. Each seed adheres to strict Genesis best practices, and has passed numerous performance, compliance and
accessibility checks. 

# Introduction

This is a template application that acts as a guide for how to build a representative set of functionality using the Genesis platform

The use-case is a position keeping system which has market data integration, and leverages multiple Genesis platform components. 
Users can input trades and see their positions update in real-time on the home screen, as well as sample charts, manage reference data, and create Notifications and Reports.
The template attempts to make use of the lastest and greatest features from the Genesis platform. The components diagram below provides an overview of those used

At a high-level, you can find:
- The positions aggregation logic [here](./server/position-app/src/main/genesis/scripts/position-consolidator.kts).
- Trade state machine logic [here](./server/position-app/src/main/kotlin/global/genesis/TradeStateMachine.kt), and how it is implemented in a Genesis Event Handler [here](./server/position-app/src/main/genesis/scripts/position-eventhandler.kts)
- Sample Java code for a B-Pipe integration [here](./server/position-bbg-adaptor/src/main/java/global/genesis/bbg/mktdata/adapter/BbgMarketDataSubscription.java).
- Web Layout Configuration [here](./client/src/routes/home/home.template.ts).

Please see here a demo video of the application in action - [Demo Video](https://vimeo.com/949678241).

![Positions App Architecture](https://github.com/genesislcap/position-sales-engineering/assets/90845530/10e80bb0-42f7-4c88-8d7a-1e06abe29c7c)


## Next Steps
As next steps to have the application running check the [See it work](https://docs.genesis.global/secure/getting-started/go-to-the-next-level/see-it-work/) guide.

If you are looking for advanced modules and how to use them head to [Go To The Next Level](https://docs.genesis.global/secure/getting-started/go-to-the-next-level/introduction/)

If you need an introduction to the Genesis platform and its modules it's worth heading [here](https://docs.genesis.global/secure/getting-started/learn-the-basics/simple-introduction/).

## Project Structure

This project contains **client** and **server** sub-project which contain respectively the frontend and the backend code

### Server

The server code for this project can be found [here](./server/README.md).
It is built using a DSL-like definition based on the Kotlin language: GPAL.

## Clients

The web client for this project can be found [here](./client/README.md). It is built using Genesis's next
generation web development framework, which is based on Web Components. Our state-of-the-art design system and component
set is built on top of [Microsoft FAST](https://www.fast.design/docs/introduction/).

# License

This is free and unencumbered software released into the public domain.

For full terms, see [LICENSE](./LICENSE)

**NOTE** This project uses licensed components listed in the next section, thus licenses for those components are required during development.

## Licensed components
Genesis low-code platform
