import { html, ref, repeat } from '@microsoft/fast-element';
import { Instruments } from './instruments';
import { sync } from '@genesislcap/foundation-utils';
import { INPUT_MIN_LENGTH, getCriteriaBuilder} from '@genesislcap/foundation-ui';

export const instrumentColumnDefs: any[] = [
  { field: 'INSTRUMENT_ID', headerName: 'ID' },
  { field: 'NAME', headerName: 'Name'},
  { field: 'BBG_INSTRUMENT_ID', headerName: 'BBG ID'},
  { field: 'REFINITIV_INSTRUMENT_ID', headerName: 'Refinitiv ID'},
  { field: 'INSTRUMENT_LIMIT', headerName: 'Limit', editable: true},
  { field: 'COUNTRY_CODE', headerName: 'Country Code'},
  { field: 'ASSET_CLASS', headerName: 'Asset Class'},
];

const searchBarConfig = [
  {
    field: 'INSTRUMENT_ID',
    label: (searchTerm) => `${searchTerm} as Instrument ID`,
    createCriteria: getCriteriaBuilder,
    isEnabled: (searchTerm, selectedOption) => {
      return (
        !selectedOption.some((e) => e.field === 'INSTRUMENT_ID')
      );
    },
  },
  {
    field: 'BBG_INSTRUMENT_ID',
    label: (searchTerm) => `${searchTerm} as BBG ID`,
    createCriteria: getCriteriaBuilder,
    isEnabled: (searchTerm, selectedOption) => {
      return (
        !selectedOption.some((e) => e.field === 'BBG_INSTRUMENT_ID')
      );
    },
  },
  {
    field: 'NAME',
    label: (searchTerm) => `${searchTerm} as Name`,
    createCriteria: getCriteriaBuilder,
    isEnabled: (searchTerm, selectedOption) => {
      return (
        !selectedOption.some((e) => e.field === 'NAME')
      );
    },
  },
];


export const InstrumentsTemplate = html<Instruments>`
  <entity-management
    title="Instruments"
    :columns=${() => instrumentColumnDefs}
    resourceName="ALL_INSTRUMENT_SUBSCRIPTION"
    createEvent="EVENT_INSTRUMENT_INSERT_SUBSCRIPTION"
    updateEvent="EVENT_UPDATE_INSTRUMENT_LIMITS"
    deleteEvent="EVENT_INSTRUMENT_DELETE"
    enable-search-bar
    :searchBarConfig="${(x) => searchBarConfig}"
  >
    <div slot="edit">
      <zero-flex-layout class="flex-column spacing-2x">
        <zero-text-field :value=${sync((x) => x.instrumentId)}>Instrument ID</zero-text-field>
        <zero-text-field :value=${sync((x) => x.name)}>Instrument Name</zero-text-field>
        <span>Country Code</span>
        <zero-combobox :value=${sync((x) => x.countryCode)} autocomplete="both">
          <zero-option value="UK">UK</zero-option>
          <zero-option value="USA">USA</zero-option>
        </zero-combobox>
        <zero-button @click=${(x) => x.createInstrumentSubscription()}>Submit</zero-button>
      </zero-flex-layout>
    </div>
  </entity-management>
`;
