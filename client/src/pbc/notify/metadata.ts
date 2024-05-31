import type { AppMetadata } from '@genesislcap/foundation-shell/app';

/**
 * @public
 */
export const metadata: AppMetadata = {
  name: '@genesislcap/pbc-notify-ui',
  description: 'Genesis Notify PBC',
  version: '0.0.3',
  prerequisites: {
    '@genesislcap/foundation-ui': '14.*',
    gsf: '7.*',
  },
  dependencies: {
    '@genesislcap/pbc-notify-ui': '1.0.11',
    '@genesislcap/foundation-notifications': '14.*',
    serverDepId: '7.1.1-SNAPSHOT',
  },
};
