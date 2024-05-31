import { customElement, FASTElement, observable } from '@microsoft/fast-element';
import { advancedFilterTemplate } from './advancedfilter.template';
import { INSTRUMENT_EVENTS, MISC_EVENTS, Store, StoreEventDetailMap, TRADE_EVENTS } from '../../../store';
import { AdvancedFilterStyles } from './advancedfilter.styles';
import { Connect } from '@genesislcap/foundation-comms';

@customElement({
  name: 'advanced-filter',
  template: advancedFilterTemplate,
  styles: AdvancedFilterStyles
})

export class AdvancedFilter extends FASTElement {
  @Connect connect: Connect;
  @Store store: Store;
  @observable segmentedInstrumentValue;
  segmentedInstrumentValueChanged(oldValue, newValue) {
    console.log("NewValue:", newValue); // Add this line to log the payload
    this.$emit(INSTRUMENT_EVENTS.EVENT_SEGMENTED_SELECTED_INSTRUMENT_CHANGE, newValue);
  }
  @observable tradesCriteria;
  tradesCriteriaChanged(oldValue, newValue) {
    this.$emit(INSTRUMENT_EVENTS.EVENT_POSITIONS_FILTERS_CHANGE, newValue);
  }

  public async generateTradeReport() {
    await this.connect.commitEvent(
      'EVENT_CREATE_DAILY_TRADE_REPORT',
      {
        DETAILS: {
          TEMPLATE_ID: "tradereporttemplate",
        },
      }
    );
  }
}
