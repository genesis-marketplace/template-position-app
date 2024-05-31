import { css } from '@microsoft/fast-element';
import { mixinScreen } from '../../../styles';

export const AdvancedFilterStyles = css`
  :host {
    ${mixinScreen('flex')}

    align-items: center;
    justify-content: center;
    flex-direction: row;

    --zero-card-fill-color: rgb(34, 39, 42);
    --neutral-stroke-divider-rest: var(--neutral-fill-stealth-rest);
    --action-height-multiplier: 1px;
  }

  criteria-segmented-control {
    display: flex;
    justify-content: center;
  }

  .filter-card {
    padding-top: 10px;
    width: 22%;
    margin-right: 10px;
  }

  .filters-label {
    width: 100%;
    color: rgb(135, 155, 166);
    text-align: center;
  }
`;
