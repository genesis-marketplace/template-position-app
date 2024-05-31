import { customElement, FASTElement, observable } from '@microsoft/fast-element';
import { RefDataTemplate as template } from './refdata.template';
import { RefDataStyles as styles } from './refdata.styles';
import { Auth, Connect } from '@genesislcap/foundation-comms';
import { FoundationLayout } from '@genesislcap/foundation-layout';


const name = 'ref-data-route';

@customElement({
  name,
  template,
  styles,
})
export class RefData extends FASTElement {
  @Connect connect: Connect;
  @Auth auth: Auth;

  refDataLayout: FoundationLayout;

}
