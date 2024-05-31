import { html, ref, repeat } from '@microsoft/fast-element';
import { BbgInstruments } from './bbginstruments';
import { sync } from '@genesislcap/foundation-utils';
import { INPUT_MIN_LENGTH, getCriteriaBuilder} from '@genesislcap/foundation-ui';

export const bbgColumnDefs: any[] = [
  { field: 'INSTRUMENT_ID', headerName: 'ID' },
  { field: 'INSTRUMENT_CODE', headerName: 'BBG Ticker' },
  { field: 'EID', headerName: 'EID' },
  { field: 'EMS_BID_PRICE', headerName: 'Bid', enableCellChangeFlash: true  },
  { field: 'EMS_ASK_PRICE', headerName: 'Ask', enableCellChangeFlash: true },
  { field: 'OPEN_PRICE', headerName: 'Last Px', enableCellChangeFlash: true },
  { field: 'BID_SIZE', headerName: 'Bid Size', enableCellChangeFlash: true },
  { field: 'ASK_SIZE', headerName: 'Ask Size', enableCellChangeFlash: true },
  { field: 'LAST_TRADE', headerName: 'Last Trade', enableCellChangeFlash: true },
];


export const BbgInstrumentsTemplate = html<BbgInstruments>`
  <entity-management
    :columns=${() => bbgColumnDefs}
    title="BBG Data"
    resourceName="ALL_BBG_INSTRUMENT_SUBSCRIPTION"
    createEvent="EVENT_ALT_INSTRUMENT_ID_INSERT"
    deleteEvent="EVENT_ALT_INSTRUMENT_ID_DELETE"
    ${ref('bbg_instruments')}
  >
    <div slot="edit">
      <zero-flex-layout class="flex-column spacing-2x">
        <span>Instrument</span>
        <zero-combobox :value=${sync((x) => x.instrumentId)}>
          <options-datasource
            resourcename="ALL_INSTRUMENTS"
            label-field="INSTRUMENT_ID"
            value-field="INSTRUMENT_ID"
          ></options-datasource>
        </zero-combobox>
        <zero-text-field readonly placeholder="BBG">Data Provider</zero-text-field>
        <zero-text-field :value=${sync((x) => x.instrumentCode)}>BBG Ticker</zero-text-field>=
        <zero-button @click=${(x) => x.createInstrumentSubscription()}>Submit</zero-button>
      </zero-flex-layout>
    </div>
  </entity-management>
`;
