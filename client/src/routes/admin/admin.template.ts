import { html } from '@microsoft/fast-element';
import type { Admin } from './admin';

// Example html with the user management
// You can customise this with additional fields, see further in this documentation
export const AdminTemplate = html<Admin>/*html*/`

<div class="container">
    <zero-tabs class="protected-tabs" activeid="userTab">
      <zero-tab id="userTab"  slot="userTab" >Users</zero-tab>
      <zero-tab id="profileTab"  slot="profileTab">Profiles</zero-tab>

      <zero-tab-panel  id="userTabContent" slot="userTabContent">
        <user-management ></user-management>
      </zero-tab-panel>

      <zero-tab-panel id="profileTabContent" slot="profileTabContent">
        <profile-management ></profile-management>
      </zero-tab-panel>

    </zero-tabs>
  </div>
</div>
`;