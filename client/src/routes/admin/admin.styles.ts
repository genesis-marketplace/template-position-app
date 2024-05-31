import { css } from '@microsoft/fast-element';
import { stylesFontFaces, mixinScreen, } from '../../styles';

export const AdminStyles = css`
:host {
    ${mixinScreen('flex')}
    align-items: left;
    justify-content: left;
    flex-direction: column;
    --accent-fill-rest: #0154d6;
    overflow-x: hidden;
  }
  :host,
  zero-design-system-provider,
  #dynamicTemplate,

  .container {
    display: flex;
    flex-direction: row;
    align-items: flex-start;
    height: 100%;
    width: calc(100% - 32px);
    padding: 16px;
    background-color: #181a1f;
  }
  main {
    height: 100%;
    width: 85%;
    height: calc(100% - 32px);
    overflow-y: auto;
  }
  #userTabContent {
    height: 100%;
    margin-top: 1px;
  }
  #profileTabContent {
    height: 100%;
    margin-top: 1px;
  }

  .protected-tabs {
    width: 100%;
    height: 100%;
  }

  zero-card {
    background-color: #1c2024;
  }

  .main-card {
    width: calc(100% - 8px);
    height: calc(100% - 3px);
  }
  `
