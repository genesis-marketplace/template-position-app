import { html } from '@microsoft/fast-element';
import { TradeStateChart } from './tradestatechart';

export const tradeStateChartTemplate = html<TradeStateChart>`
  <template>
    <zero-card class="chart-card">
      <zero-g2plot-chart type="column" :config=${(x) => x.stateChartConfiguration}>
        <chart-datasource
          resourceName="ALL_TRADES"
          server-fields="TRADE_DATE CONSIDERATION OVERALL_STATUS"
          chart-fields="trade_date value tradeStatus"
          isSnapshot="false"
          maxRows="1000"
          orderBy="TRADE_DATE"
          criteria=${(x) => x.store.lineChartFilterCriteria}
        ></chart-datasource>
      </zero-g2plot-chart>
    </zero-card>
  </template>
`;
