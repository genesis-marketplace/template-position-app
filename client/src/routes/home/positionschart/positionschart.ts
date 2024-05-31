import { customElement, FASTElement, observable } from '@microsoft/fast-element';
import { Store } from '../../../store';
import { positionsChartTemplate } from './positionschart.template';

@customElement({
  name: 'positions-chart',
  template: positionsChartTemplate,
})
export class PositionsChart extends FASTElement {
  @Store store: Store;
  @observable chartsConfiguration = {
    angleField: 'value',
    colorField: 'groupBy',
    label: {
      type: 'spider',
      labelHeight: 28,
      content: '{name}\n{percentage}',
      style: {
        fill: 'white',
      },
    },
    interactions: [{ type: 'element-selected' }, { type: 'element-active' }],
    statistic: {
      title: false,
      content: {
        style: {
          whiteSpace: 'pre-wrap',
          overflow: 'hidden',
          textOverflow: 'ellipsis',
        },
        content: 'Positions breakdown',
      },
    },
  };
}
