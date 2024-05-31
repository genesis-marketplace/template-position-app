import { html, ref, repeat } from '@microsoft/fast-element';
import { CounterPartys } from './counter-partys';
import { sync } from '@genesislcap/foundation-utils';

export const counterpartyColumnDefs: any[] = [
  { field: 'COUNTERPARTY_ID', headerName: 'ID' },
  { field: 'NAME', headerName: 'Name', editable: true },
  { field: 'ENABLED', headerName: 'Enabled', editable: true },
  { field: 'COUNTERPARTY_LEI', headerName: 'LEI', editable: true, enableCellChangeFlash: true }
];

export const CounterPartysTemplate = html<CounterPartys>`
  <entity-management
    title="Counterpartys"
    :columns=${() => counterpartyColumnDefs}
    resourceName="ALL_COUNTERPARTYS"
    createEvent="EVENT_COUNTERPARTY_INSERT"
    updateEvent="EVENT_COUNTERPARTY_MODIFY"
    deleteEvent="EVENT_COUNTERPARTY_DELETE"
  >
  </entity-management>
`;
