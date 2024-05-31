import { html, ref, repeat } from '@microsoft/fast-element';
import { MarketData } from './market-data';
import { sync } from '@genesislcap/foundation-utils';

export const marketDataColumnDefs: any[] = [
  { field: 'SUBSCRIPTION_NAME', headerName: 'Name' },
  { field: 'SUBSCRIPTION_STATUS', headerName: 'Status', editable: true }
];

export const MarketDataTemplate = html<MarketData>`
  <entity-management
    title="Market Data Subscriptions"
    :columns=${() => marketDataColumnDefs}
    resourceName="ALL_MARKET_DATA_SUBSCRIPTION"
    updateEvent="EVENT_MANAGE_MARKET_DATA_SUBSCRIPTION"
  >
  </entity-management>
`;
