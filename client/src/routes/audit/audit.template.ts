import { html } from '@microsoft/fast-element';
import type { Audit } from './audit';

export const AuditTemplate = html<Audit>`
  <zero-card class="positions-card">
    <span class="card-title">Trade Audit (infinite scroll)</span>
    <zero-grid-pro>
      <grid-pro-genesis-datasource resource-name="ALL_TRADES_AUDIT" />
    </zero-grid-pro>
  </zero-card>
`;
