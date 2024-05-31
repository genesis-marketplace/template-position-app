import { customElement, FASTElement, observable } from '@microsoft/fast-element';
import { BbgInstrumentsTemplate } from './bbginstruments.template';
import { Auth, Connect } from '@genesislcap/foundation-comms';
import { EntityManagement } from '@genesislcap/foundation-entity-management';

@customElement({
  name: 'bbg-instruments-route',
  template: BbgInstrumentsTemplate,
})
export class BbgInstruments extends FASTElement {
  @Connect connect: Connect;
  @Auth auth: Auth;

  @observable instrumentId: string = '';
  @observable instrumentCode: string = '';
  @observable bbg_instruments: EntityManagement;

  public async createInstrumentSubscription() {
    await this.connect.commitEvent(
      'EVENT_ALT_INSTRUMENT_ID_INSERT',
      {
        DETAILS: {
          INSTRUMENT_ID: this.instrumentId,
          ALTERNATE_TYPE: "BBG",
          INSTRUMENT_CODE: this.instrumentCode,
        },
      }
    );
    this.bbg_instruments.closeModal();
  }
}
