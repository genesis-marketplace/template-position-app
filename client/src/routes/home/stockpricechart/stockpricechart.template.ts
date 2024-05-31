import { html } from '@microsoft/fast-element';
import { StockPriceChart } from './stockpricechart';

export const stockPriceChartTemplate = html<StockPriceChart>`
  <template>
    <zero-card class="chart-card">
      <zero-g2plot-chart type="area" :config=${(x) => x.stockChartConfiguration}>
        <chart-datasource
          resourceName="ALL_INSTRUMENT_PRICE_HISTORY"
          server-fields="MARKET_DATE OPEN_PRICE INSTRUMENT_ID"
          chart-fields="date open instrumentId"
          isSnapshot="false"
          maxRows="1000"
          orderBy="MARKET_DATE"
          criteria=${(x) => x.store.stockChartFilterCriteria}
        ></chart-datasource>
      </zero-g2plot-chart>
    </zero-card>
  </template>
`;
