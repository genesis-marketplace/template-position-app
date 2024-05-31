import { html, ref, repeat } from '@microsoft/fast-element';
import { tradeColumnDefs } from './tradeColumnDefs';
import { tradesGridStyles } from './trades-grid.styles';
import { TradesGrid } from './tradesgrid';

export const tradesGridTemplate = html<TradesGrid>`
  <template>
    <zero-card class="trades-card">
      <zero-grid-pro
        rowHeight="40"
        only-template-col-defs
        ${ref('tradeGrid')}
        enabledRowFlashing
        pagination="true"
        >
        <grid-pro-genesis-datasource
          resource-name="ALL_TRADES"
          order-by="ALL_TRADES_BY_TRADE_DATETIME_TRADE_ID"
          reverse=true
          :deferredGridOptions=${(x) => x.tradesGridOptions}
          criteria=${(x) => x.store.tradesFilterCriteria}
          ${ref('tradesDatasource')}
        ></grid-pro-genesis-datasource>
        <slotted-styles :styles=${() => tradesGridStyles}></slotted-styles>
        ${repeat(
          () => tradeColumnDefs,
          html`
            <grid-pro-column :definition="${(x) => x}" />
          `
        )}
        <grid-pro-column :definition="${(x) => x.allocateTradeActionColDef}" />
        <grid-pro-column :definition="${(x) => x.cancelTradeActionColDef}" />
      </zero-grid-pro>
    </zero-card>
  </template>
`;
