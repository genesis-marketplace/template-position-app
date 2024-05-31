import { html } from '@microsoft/fast-element';
import { AdvancedFilter } from './advancedfilter';
import { CriteriaSegmentedControlOption, Serialisers } from '@genesislcap/foundation-criteria';
import { sync } from '@genesislcap/foundation-utils';
import { allPositionsFilterSchema } from './tradeFilterSchema';



const toolbarOptions: CriteriaSegmentedControlOption[] = [
  { label: 'VOD', field: 'INSTRUMENT_ID', value: 'VOD', serialiser: Serialisers.EQ },
  { label: 'LSEG', field: 'INSTRUMENT_ID', value: 'LSEG', serialiser: Serialisers.EQ },
  { label: 'LLOY', field: 'INSTRUMENT_ID', value: 'LLOY', serialiser: Serialisers.EQ },
  { label: 'BP', field: 'INSTRUMENT_ID', value: 'BP', serialiser: Serialisers.EQ },
  { label: 'AZN', field: 'INSTRUMENT_ID', value: 'AZN', serialiser: Serialisers.EQ },
];

export const advancedFilterTemplate = html<AdvancedFilter>`
  <template>
    <zero-card class="trades-card">
      <criteria-segmented-control
        :criteriaOptions=${() => toolbarOptions}
        :value=${sync((x) => x.segmentedInstrumentValue)}
      >
        <h4 slot="label" class="filters-label">Trades Filter</h4>
      </criteria-segmented-control>
      <zero-button @click=${x => x.generateTradeReport()}>Generate Trade Report</zero-button>
      <h4 class="filters-label">Positions Filter</h4>
      <foundation-filters
        style="height: fit-content;"
        :value=${sync((x) => x.tradesCriteria)}
        :uischema=${() => allPositionsFilterSchema}
        resourceName="ALL_POSITIONS"
      ></foundation-filters>
    </zero-card>
  </template>
`;
