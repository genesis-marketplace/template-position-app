import { html, ref, when } from '@microsoft/fast-element';
import type { Home } from './home';
import { addTradeFormSchema } from './addTradeFormSchema';
import { TradesChart } from './tradeschart';
import { PositionsChart } from './positionschart';
import { TradesGrid } from './tradesgrid';
import { PositionsGrid } from './positionsgrid';
import { StockPriceChart } from './stockpricechart';
import { TradeStateChart } from './tradestatechart'
import { AdvancedFilter } from './advancedfilter';
import { sync } from '@genesislcap/foundation-utils';

PositionsGrid;
TradesGrid;
PositionsChart;
TradesChart;
StockPriceChart;
AdvancedFilter;
TradeStateChart;

export const HomeTemplate = html<Home>`
  <template>
    <zero-error-boundary>
      <genesis-notification></genesis-notification>
    </zero-error-boundary>

    <zero-modal
      ${ref('addTradeModal')}
      position="right"
      :onCloseCallback=${(x) => x.closeNavCallback.bind(x)}
    >
      ${when(
        (x) => !x.store.sideNavClosed,
        html<Home>`
          <foundation-form
            style="height: 100%;"
            resourceName=${() => 'EVENT_TRADE_INSERT'}
            :data=${(x) => x.store.addTradeData}
            :uischema=${() => addTradeFormSchema}
            @submit-success=${(x) => x.closeNavCallback()}
          ></foundation-form>
        `
      )}
    </zero-modal>

    <zero-layout auto-save-key="position-app-seed-layout" ${ref('layout')} popout-config="960;720">
      <zero-layout-region type="vertical">
        <zero-layout-item title="Filter"  size="25%" closable>
          <advanced-filter></advanced-filter>
        </zero-layout-item>
        <zero-layout-region type="horizontal">
          <zero-layout-region type="tabs">
            <zero-layout-item title="Positions Table">
              <positions-grid></positions-grid>
            </zero-layout-item>
            <zero-layout-item title="Positions Chart">
              <positions-chart></positions-chart>
            </zero-layout-item>
          </zero-layout-region>
          <zero-layout-region type="tabs">
            <zero-layout-item title="Trades Table">
              <trades-grid></trades-grid>
            </zero-layout-item>
            <zero-layout-item title="Trades Chart">
              <trades-chart></trades-chart>
            </zero-layout-item>
            <zero-layout-item title="Stock Price Chart">
              <stock-price-chart></stock-price-chart>
            </zero-layout-item>
            <zero-layout-item title="Trade State Chart">
              <trade-state-chart></trade-state-chart>
            </zero-layout-item>
          </zero-layout-region>
        </zero-layout-region>
      </zero-layout-region>
    </zero-layout>
  </template>
`;
