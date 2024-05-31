import { customElement, FASTElement, observable } from '@microsoft/fast-element';
import { chartsGradients } from '@genesislcap/g2plot-chart';
import { Store } from '../../../store';
import { stockPriceChartTemplate } from './stockpricechart.template';

@customElement({
  name: 'stock-price-chart',
  template: stockPriceChartTemplate,
})
export class StockPriceChart extends FASTElement {
  @Store store: Store;

  @observable stockChartConfiguration = {
    padding: 'auto',
    seriesField: 'instrument_id',
    xField: 'date',
    yField: 'open',
    xAxis: {
      type: 'time',
      tickCount: 10,
    },
    slider: {
      start: 0,
      end: 1.0,
    },
  };
}
