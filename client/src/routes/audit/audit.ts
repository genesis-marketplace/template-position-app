import { customElement, FASTElement } from '@microsoft/fast-element';
import { AuditTemplate as template } from './audit.template';
import { AuditStyles as styles } from './audit.styles';

@customElement({
  name: 'audit-route',
  template,
  styles,
})
export class Audit extends FASTElement {}
