import { customElement, FASTElement } from '@microsoft/fast-element';
import { AdminTemplate as template } from './admin.template';
import { AdminStyles as styles } from './admin.styles';

const name = 'admin-route';

@customElement({
  name,
  template,
  styles,
})
export class Admin extends FASTElement {
  constructor() {
    super();
  }
}
