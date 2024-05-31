import { customElement, FASTElement, observable } from '@microsoft/fast-element';
import { chartsGradients } from '@genesislcap/g2plot-chart';
import { Store } from '../../../store';
import { tradesChartTemplate } from './tradeschart.template';

@customElement({
  name: 'trades-chart',
template: tradesChartTemplate,
})
export class TradesChart extends FASTElement {
  @Store store: Store;

  @observable lineChartConfiguration = {
    padding: 'auto',
    seriesField: 'instrument_id',
    xField: 'trade_date',
    yField: 'value',
    xAxis: {
      type: 'time',
      tickCount: 10,
    },
    slider: {
      start: 0.7,
      end: 1.0,
    },
    color: [chartsGradients.rapidGreen, chartsGradients.rapidRed, chartsGradients.rapidBlue, chartsGradients.rapidIce],
  };
}
