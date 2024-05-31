import { html, ref, repeat } from '@microsoft/fast-element';
import { positionColumnDefs } from './positionColumnDefs';
import { PositionsGrid } from './positionsgrid';

export const positionsGridTemplate = html<PositionsGrid>`
  <template>
    <zero-card class="positions-card">
      <zero-grid-pro rowHeight="40" only-template-col-defs ${ref('positionsGrid')} persist-filter-model-key="grid-pro-events-filter-model">
        <grid-pro-genesis-datasource
          resource-name="ALL_POSITIONS"
          order-by="INSTRUMENT_ID"
          :deferredGridOptions=${(x) => x.positionsGridOptions}
          criteria=${(x) => x.store.positionsFilterCriteria}
          ${ref('positionsDatasource')}
        ></grid-pro-genesis-datasource>
        ${repeat(
          () => positionColumnDefs,
          html`
            <grid-pro-column :definition="${(x) => x}"/>
          `
        )}
        <grid-pro-column :definition="${(x) => x.singlePositionActionColDef}"/>
      </zero-grid-pro>
    </zero-card>
  </template>
`;
