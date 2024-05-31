import { css, ElementStyles } from '@microsoft/fast-element';

const BUY_SIDE = '#48ED9C';
const SELL_SIDE = '#F44378';

const NEW_TRADE_STATUS = '#486CED';
const ALLOCATED_TRADE_STATUS = '#48ED9C';
const CANCELLED_TRADE_STATUS = '#F44378';

const MATCHED_TRADE_STATUS = '#48ED9C';
const UNMATCHED_TRADE_STATUS = '#F44378';

export const tradesGridStyles: ElementStyles = css`
  .status-cell {
    display: flex;
    align-items: center;
    margin-left: 6px;
  }

  .status-cell::after {
    content: '';
    position: absolute;
    left: 6px;
    height: 100%;
    width: 3px;
  }

  .buy-side-trade.status-cell::after {
    background-color: ${BUY_SIDE};
  }

  .buy-side-trade {
    color: ${BUY_SIDE};
  }

  .sell-side-trade.status-cell::after {
    background-color: ${SELL_SIDE};
  }

  .sell-side-trade {
    color: ${SELL_SIDE};
  }

  .new-status-trade.status-cell::after {
    background-color: ${NEW_TRADE_STATUS};
  }

  .new-status-trade {
    color: ${NEW_TRADE_STATUS};
  }

  .cancel-status-trade.status-cell::after {
    background-color: ${CANCELLED_TRADE_STATUS};
  }

  .cancel-status-trade {
    color: ${CANCELLED_TRADE_STATUS};
  }

  .allocated-status-trade.status-cell::after {
    background-color: ${ALLOCATED_TRADE_STATUS};
  }

  .allocated-status-trade {
    color: ${ALLOCATED_TRADE_STATUS};
  }

  .matched-trade.status-cell::after {
    background-color: ${MATCHED_TRADE_STATUS};
  }

  .matched-trade {
    color: ${MATCHED_TRADE_STATUS};
  }

  .unmatched-trade.status-cell::after {
    background-color: ${UNMATCHED_TRADE_STATUS};
  }

  .unmatched-trade {
    color: ${UNMATCHED_TRADE_STATUS};
  }
`;
