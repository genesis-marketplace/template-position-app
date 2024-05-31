import { html } from '@microsoft/fast-element';
import { PositionsChart } from './positionschart';

export const positionsChartTemplate = html<PositionsChart>`
  <template>
    <zero-card class="chart-card">
      <zero-g2plot-chart type="donut" :config=${(x) => x.chartsConfiguration}>
        <chart-datasource
          resourceName="ALL_POSITIONS"
          server-fields="INSTRUMENT_ID CURRENT_VALUE"
          charts-fields="groupBy value"
          isSnapshot="false"
        ></chart-datasource>
      </zero-g2plot-chart>
    </zero-card>
  </template>
`;
