import { customElement, FASTElement, observable } from '@microsoft/fast-element';
import { MarketDataTemplate } from './market-data.template';
import { Auth, Connect } from '@genesislcap/foundation-comms';
import { EntityManagement } from '@genesislcap/foundation-entity-management';

@customElement({
  name: 'market-data-route',
  template: MarketDataTemplate,
})
export class MarketData extends FASTElement {
  @Connect connect: Connect;
  @Auth auth: Auth;

}
