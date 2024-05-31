import { customElement, FASTElement, observable } from '@microsoft/fast-element';
import { ColDef, RowSelectedEvent, CellClickedEvent } from '@ag-grid-community/core';
import { INSTRUMENT_EVENTS, MISC_EVENTS, Store, StoreEventDetailMap, TRADE_EVENTS } from '../../../store';
import { GridProGenesisDatasource, ZeroGridPro } from '@genesislcap/foundation-zero-grid-pro';
import { positionsGridTemplate } from './positionsgrid.template';
import { EventEmitter } from '@genesislcap/foundation-events';
import { sendEventOnChannel } from '../../../utils';
import * as fdc3 from '@finos/fdc3';

export type EventMap = StoreEventDetailMap;

@customElement({
  name: 'positions-grid',
  template: positionsGridTemplate,
})
export class PositionsGrid extends EventEmitter<EventMap>(FASTElement) {
  @Store store: Store;
  public positionsGrid!: ZeroGridPro;
  public positionsDatasource!: GridProGenesisDatasource;

  private sources: Map<string, GridProGenesisDatasource> = new Map<
    string,
    GridProGenesisDatasource
  >();

  connectedCallback(): void {
    super.connectedCallback();
    this.sources.set('positionsDatasource', this.positionsDatasource);
  }

  public disconnectedCallback(): void {
    super.disconnectedCallback();
  }

  handleFilterChanged = (e) => {
    const fieldName = e?.detail?.fieldName;
    const filter = e?.detail?.filter;
    const target: string = e?.detail?.target;

    this.positionsGrid.gridApi.deselectAll();

    fieldName && filter && this.sources.get(target)?.setFilter(fieldName, filter);
  };

  handleFilterCleared = (e) => {
    const fieldName = e?.detail?.fieldName;
    const target: string = e?.detail?.target;

    this.positionsGrid.gridApi.deselectAll();

    this.sources.get(target)?.removeFilter(fieldName);
  };

  @observable positionsGridOptions = {
    suppressRowClickSelection: true,
    onCellClicked: (e: CellClickedEvent) => {
      if (e.column.getColId() !== 'ADD_TRADE' || !e.node.isSelected()) {
        e.node.setSelected(!e.node.isSelected());
      }
    },
    onRowClicked: async (e: RowSelectedEvent) => {
      const payload = e.node.isSelected() ? e.data : undefined;
      this.$emit(INSTRUMENT_EVENTS.EVENT_SELECTED_INSTRUMENT_CHANGE, payload);
      sendEventOnChannel("ViewInstrumentChannel", "ViewInstrument.result");
    },
    defaultColDef: {
      wrapHeaderText: true,
      resizable: true,
      autoHeaderHeight: true,
      floatingFilter: true,
      sortable: true,
      filter: true,
      enableRowGroup: true,
      enablePivot: true,
      minWidth: 100,
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

  @observable fdc3ActionColDef: ColDef;

  public singlePositionActionColDef: ColDef = {
    headerName: 'Action',
    colId: 'ADD_TRADE',
    minWidth: 130,
    maxWidth: 130,
    cellRenderer: 'action', // AgRendererTypes.action
    cellRendererParams: {
      actionClick: async (rowData) => {
        this.$emit(TRADE_EVENTS.EVENT_SET_ADD_TRADES, {
          INSTRUMENT_ID: rowData.INSTRUMENT_ID,
        });
        this.$emit(MISC_EVENTS.EVENT_SIDE_NAV, 'open');
      },
      actionName: 'Add Trade',
      appearance: 'primary-gradient',
    },
    pinned: 'right',
  };
}
