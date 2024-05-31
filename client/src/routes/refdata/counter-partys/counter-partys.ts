import { customElement, FASTElement, observable } from '@microsoft/fast-element';
import { CounterPartysTemplate } from './counter-partys.template';
import { Auth, Connect } from '@genesislcap/foundation-comms';
import { EntityManagement } from '@genesislcap/foundation-entity-management';

@customElement({
  name: 'counter-partys',
  template: CounterPartysTemplate,
})
export class CounterPartys extends FASTElement {
  @Connect connect: Connect;
  @Auth auth: Auth;

}
