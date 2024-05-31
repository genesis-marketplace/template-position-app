import { customElement, FASTElement, observable } from '@microsoft/fast-element';
import { InstrumentsTemplate } from './instruments.template';
import { Auth, Connect } from '@genesislcap/foundation-comms';
import { EntityManagement } from '@genesislcap/foundation-entity-management';

@customElement({
  name: 'instruments-route',
  template: InstrumentsTemplate,
})
export class Instruments extends FASTElement {
  @Connect connect: Connect;
  @Auth auth: Auth;

  @observable instrumentId: string = '';
  @observable name: string = '';
  @observable countryCode: string = '';
  @observable bbg_instruments: EntityManagement;


  public async createInstrumentSubscription() {
    await this.connect.commitEvent(
      'EVENT_INSTRUMENT_INSERT_SUBSCRIPTION',
      {
        DETAILS: {
          INSTRUMENT_ID: this.instrumentId,
          NAME: this.name,
          INSTRUMENT_CODE: this.instrumentId,
          COUNTRY_CODE: this.countryCode,
        },
      }
    );
    this.bbg_instruments.closeModal();
  }
}
