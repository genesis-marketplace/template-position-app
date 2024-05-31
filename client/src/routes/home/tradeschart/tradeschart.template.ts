import { html } from '@microsoft/fast-element';
import { TradesChart } from './tradeschart';

export const tradesChartTemplate = html<TradesChart>`
  <template>
    <zero-card class="chart-card">
      <zero-g2plot-chart type="area" :config=${(x) => x.lineChartConfiguration}>
        <chart-datasource
          resourceName="ALL_TRADE_DAILY"
          server-fields="TRADE_DATE AVG_PRICE INSTRUMENT_ID"
          chart-fields="trade_date value instrumentId"
          isSnapshot="false"
          maxRows="1000"
          orderBy="TRADE_DATE"
          criteria=${(x) => x.store.lineChartFilterCriteria}
        ></chart-datasource>
      </zero-g2plot-chart>
    </zero-card>
  </template>
`;
