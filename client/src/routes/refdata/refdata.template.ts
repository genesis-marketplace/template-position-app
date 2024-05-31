import { sync } from '@genesislcap/foundation-utils';
import { html, ref } from '@microsoft/fast-element';
import type { RefData } from './refdata';
import { CounterPartys } from './counter-partys';
import { Instruments } from './instruments';
import { MarketData } from './market-data';
import { BbgInstruments } from './bbginstruments'

CounterPartys;
Instruments;
MarketData;
BbgInstruments;

export const RefDataTemplate = html<RefData>`
  <template>
    <zero-layout auto-save-key="ref-data-layout" ${ref('refDataLayout')}>
      <zero-layout-region type="horizontal">
        <zero-layout-item title="Counterparty">
          <counter-partys></counter-partys>
        </zero-layout-item>
        <zero-layout-region type="tabs">
          <zero-layout-item title="Instruments">
            <instruments-route></instruments-route>
          </zero-layout-item>
          <zero-layout-item title="Market Data Subscriptions">
            <market-data-route></market-data-route>
          </zero-layout-item>
          <zero-layout-item title="Bbg Instruments">
            <bbg-instruments-route></bbg-instruments-route>
          </zero-layout-item>
        </zero-layout-region>
      </zero-layout-region>
    </zero-layout>
  </template>
`;
