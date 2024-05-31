import { ColDef } from '@ag-grid-community/core';
import { formatDateLong, formatNumber, formatDateTime } from '../../../utils/formatting';
import { SIDE, TradeStatus, TradeMatchStatus } from '../../../types';

const tradeCellClassRules = {
  'buy-side-trade': (params) => params.value === SIDE.BUY,
  'sell-side-trade': (params) => params.value === SIDE.SELL,
  'new-status-trade': (params) => params.value === TradeStatus.NEW,
  'allocated-status-trade': (params) => params.value === TradeStatus.ALLOCATED,
  'cancel-status-trade': (params) => params.value === TradeStatus.CANCELLED,
  'matched-trade': (params) => params.value === TradeMatchStatus.MATCHED,
  'unmatched-trade': (params) => params.value === TradeMatchStatus.UNMATCHED,
};
export const tradeColumnDefs: ColDef[] = [
  { field: 'INSTRUMENT_NAME', headerName: 'Instrument'},
  {
    field: 'SIDE',
    headerName: 'Side',
    cellClass: 'status-cell',
    cellClassRules: tradeCellClassRules,
    enableCellChangeFlash: true,
    minWidth: 80,
    flex: 1,
  },
  {
    field: 'QUANTITY',
    headerName: 'Quantity',
    valueFormatter: formatNumber(0),
    type: 'rightAligned',
    enableCellChangeFlash: true,
    flex: 1,
  },
  {
    field: 'PRICE',
    headerName: 'Price',
    valueFormatter: formatNumber(2),
    type: 'rightAligned',
    enableCellChangeFlash: true,
    flex: 2,
  },
  {
    field: 'CONSIDERATION',
    headerName: 'Consideration',
    valueFormatter: formatNumber(2),
    type: 'rightAligned',
    enableCellChangeFlash: true,
    flex: 2,
  },
  {
    field: 'TRADE_DATETIME',
    headerName: 'Date',
    valueFormatter: (rowData) => formatDateTime(rowData.data.TRADE_DATETIME),
    sort: 'desc',
    enableCellChangeFlash: true,
    flex: 2,
  },
  {
    field: 'COUNTERPARTY_NAME',
    headerName: 'Counterparty',
    enableCellChangeFlash: true,
    flex: 2,
  },
  {
    field: 'TRADE_STATUS',
    headerName: 'Trade State',
    cellClass: 'status-cell',
    cellClassRules: tradeCellClassRules,
    enableCellChangeFlash: true,
    editable: true,
    flex: 2,
  },
 {
    field: 'TRADE_MATCH_STATUS',
    headerName: 'Match Status',
    cellClass: 'status-cell',
    cellClassRules: tradeCellClassRules,
    enableCellChangeFlash: true,
    editable: true,
    flex: 2,
  },
  {
    field: 'ENTERED_BY',
    headerName: 'Entered By',
    enableCellChangeFlash: true,
    flex: 2,
  },
];
