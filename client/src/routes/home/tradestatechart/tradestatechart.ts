import { customElement, FASTElement, observable } from '@microsoft/fast-element';
import { chartsGradients } from '@genesislcap/g2plot-chart';
import { Store } from '../../../store';
import { tradeStateChartTemplate } from './tradestatechart.template';

@customElement({
  name: 'trade-state-chart',
  template: tradeStateChartTemplate,
})
export class TradeStateChart extends FASTElement {
  @Store store: Store;

  @observable stateChartConfiguration = {
    padding: 'auto',
    seriesField: 'tradeStatus',
    isStack: true,
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
