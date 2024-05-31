import type { AppMetadata } from '@genesislcap/foundation-shell/app';

/**
 * @public
 */
export const metadata: AppMetadata = {
  name: '@genesislcap/pbc-reporting',
  description: 'Genesis Reporting PBC',
  version: '1.1.0',
  prerequisites: {
    '@genesislcap/foundation-ui': '14.*',
    gsf: '7.*',
  },
  dependencies: {
    '@genesislcap/pbc-reporting-ui': '1.0.1',
    serverDepId: '7.1.1-SNAPSHOT',
  },
};
