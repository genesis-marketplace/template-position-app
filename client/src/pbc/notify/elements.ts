import type { ToastButton } from '@genesislcap/foundation-notifications';
import type { AppElement } from '@genesislcap/foundation-shell/app';
import { html, when } from '@microsoft/fast-element';
import { getNotifyStore } from './stores';
import { isInteropFdc3 } from './utils';

const store = getNotifyStore();


/**
 * @public
 */
export const notificationsButton: AppElement = {
  targetId: 'nav-end',
  elements: html`
    <div
      class="icon-container notifications-button"
      part="notifications-button"
      data-test-id="notifications-button"
      data-pbc-asset-id="notifications-button"
      @click=${(x, c) => c.parent.$emit('notification-icon-clicked')}
    >
      <zero-icon variant="regular" name="bell" part="notifications-icon"></zero-icon>
      <foundation-inbox-counter part="notifications-inbox-counter"></foundation-inbox-counter>
    </div>
  `,
};

/**
 * @public
 */
export const inboxFlyout: AppElement = {
  targetId: 'layout',
  elements: html`
    <zero-flyout
      position="right"
      @closed=${(x, c) => c.parent.$emit('change-inbox-display', false)}
      ?closed=${() => !store.inboxDisplayState}
      data-pbc-asset-id="inbox-flyout"
    >
      <div slot="title">Alerts</div>
      <foundation-inbox></foundation-inbox>
    </zero-flyout>
  `,
};

/**
 * @public
 */
export const alertsFlyout: AppElement = {
  targetId: 'layout',
  elements: html`
    <zero-flyout
      position="right"
      @closed=${(x, c) => c.parent.$emit('change-alert-rules-display', false)}
      ?closed=${() => !store.alertsRulesDisplayState}
      displayHeader=${false}
      data-pbc-asset-id="alerts-flyout"
    >
      <foundation-alerts></foundation-alerts>
    </zero-flyout>
  `,
};

/**
 * @public
 */
export const notificationListener: AppElement = {
  targetId: 'layout',
  elements: html`
    ${when(() => !isInteropFdc3(), html`
      <zero-notification-listener
        resource-name="ALL_NOTIFY_ALERT_RECORDS"
        data-pbc-asset-id="interop-notification-listener"
        :toastButtons="${(): ToastButton[] => [
          {
            condition: ({details}) => details.NOTIFY_SEVERITY !== 'Warning',
            action: ({details, buttonElement, toastElement}) => {
              console.log({details, buttonElement, toastElement});
              toastElement.close();
            },
            appearance: 'secondary-orange',
            placeholder: 'button NOT for warnings',
          },
        ]}"
      ></zero-notification-listener>
    `)}
    ${when(() => isInteropFdc3(), html`
      <zero-interop-notification-listener
        resource-name="ALL_NOTIFY_ALERT_RECORDS"
        data-pbc-asset-id="interop-notification-listener"
        :toastButtons="${(): ToastButton[] => [
          {
            condition: ({details}) => details.NOTIFY_SEVERITY !== 'Warning',
            action: ({details, buttonElement, toastElement}) => {
              console.log({details, buttonElement, toastElement});
              toastElement.close();
            },
            appearance: 'secondary-orange',
            placeholder: 'button NOT for warnings',
          },
        ]}"
      ></zero-interop-notification-listener>
    `)}
  `,
};

/**
 * @public
 */
export const bannerAnchor: AppElement = {
  targetId: 'content-start',
  elements: html`
    <div id="banner-anchor" data-pbc-asset-id="banner-anchor"></div>
  `,
};
