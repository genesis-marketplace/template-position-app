import { GridProGenesisDatasource, ZeroGridPro } from '@genesislcap/foundation-zero-grid-pro';
import { customElement, FASTElement, observable } from '@microsoft/fast-element';
import { Events, ColDef } from '@ag-grid-community/core';
import { Store, StoreEventDetailMap, TRADE_EVENTS } from '../../../store';
import { Connect } from '@genesislcap/foundation-comms';
import { logger } from '../../../utils';
import { TradeStatus } from '../../../types';
import { tradesGridTemplate } from './tradesgrid.template';
import { EventEmitter } from '@genesislcap/foundation-events';

export type EventMap = StoreEventDetailMap;
@customElement({
  name: 'trades-grid',
  template: tradesGridTemplate,
})
export class TradesGrid extends EventEmitter<EventMap>(FASTElement) {
  @Store store: Store;
  public tradeGrid!: ZeroGridPro;
  public tradesDatasource!: GridProGenesisDatasource;
  @Connect connect: Connect;

  private sources: Map<string, GridProGenesisDatasource> = new Map<
    string,
    GridProGenesisDatasource
  >();

  connectedCallback() {
    super.connectedCallback();
    this.sources.set('tradesDatasource', this.tradesDatasource);
  }

  disconnectedCallback() {
    super.disconnectedCallback();
  }


  @observable tradesGridOptions = {
    suppressRowClickSelection: true,
    defaultColDef: {
      wrapHeaderText: true,
      resizable: true,
      autoHeaderHeight: true,
      floatingFilter: true,
      sortable: true,
      filter: true,
      enableRowGroup: true,
      enablePivot: true,
      enableValue: true,
    },
    sideBar: {
      toolPanels: [
        {
          id: 'columns',
          labelDefault: 'Columns',
          labelKey: 'columns',
          iconKey: 'columns',
          toolPanel: 'agColumnsToolPanel',
        },
        {
          id: 'filters',
          labelDefault: 'Filters',
          labelKey: 'filters',
          iconKey: 'filter',
          toolPanel: 'agFiltersToolPanel',
        },
      ],
      position: 'right',
    },
  };

  public cancelTradeActionColDef: ColDef = {
    headerName: 'Cancel',
    minWidth: 110,
    maxWidth: 110,
    cellRenderer: 'action', // AgRendererTypes.action
    cellRendererParams: {
      actionClick: async (rowData) => {
        this.$emit(TRADE_EVENTS.EVENT_SET_ADD_TRADES, rowData);
        const tradeCancelEvent = await this.connect.commitEvent('EVENT_TRADE_CANCEL', {
          DETAILS: {
            TRADE_ID: this.store.addTradeData.TRADE_ID,
          },
        });

        logger.debug('EVENT_TRADE_CANCEL result -> ', tradeCancelEvent);
      },
      actionName: 'Cancel',
      appearance: 'secondary-orange',
      isDisabled: (rowData) => {
        return rowData?.TRADE_STATUS === TradeStatus.CANCELLED;
      },
    },
    pinned: 'right',
  };

  public allocateTradeActionColDef: ColDef = {
      headerName: 'Allocate',
      minWidth: 130,
      maxWidth: 130,
      cellRenderer: 'action', // AgRendererTypes.action
      cellRendererParams: {
        actionClick: async (rowData) => {
          this.$emit(TRADE_EVENTS.EVENT_SET_ADD_TRADES, rowData);
          const tradeAllocateEvent = await this.connect.commitEvent('EVENT_TRADE_ALLOCATED', {
            DETAILS: {
              TRADE_ID: this.store.addTradeData.TRADE_ID,
            },
          });

          logger.debug('EVENT_TRADE_ALLOCATED result -> ', tradeAllocateEvent);
        },
        actionName: 'Allocate',
        appearance: 'secondary-orange',
        isDisabled: (rowData) => {
          return rowData?.TRADE_STATUS !== TradeStatus.NEW;
        },
      },
      pinned: 'right',
    };
}
