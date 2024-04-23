import cn from 'classnames';
import { useMemo } from 'react';

type Recurrence = {
  interval: 'DAY' | 'WEEK' | 'MONTH' | 'YEAR';
  intervalCount: number;
};

type Props = {
  product: Product;
  onClick: (stripeId: string) => void;
  disabled?: boolean;
};

const CURRENCY_SYMBOLS: Record<string, string> = {
  EUR: '€',
  GBP: '£',
  USD: '$',
};

const generateBackgroundColor = (): string => {
  const hue = Math.floor(Math.random() * 360);
  return `hsl(${hue}, 100%, 90%)`;
};

const formatCurrency = (currency: string): string => {
  const key = currency.toUpperCase();
  return CURRENCY_SYMBOLS[key] || key;
};

const formatRecurrence = ({ interval, intervalCount }: Recurrence): string => {
  switch (interval) {
    case 'DAY':
      return intervalCount === 1 ? 'per day' : `every ${intervalCount} days`;
    case 'WEEK':
      return intervalCount === 1 ? 'per week' : `every ${intervalCount} weeks`;
    case 'MONTH':
      return intervalCount === 1
        ? 'per month'
        : `every ${intervalCount} months`;
    case 'YEAR':
      return intervalCount === 1 ? 'per year' : `every ${intervalCount} years`;
  }
};

const CustomerProductCardComponent = ({
  product,
  onClick,
  disabled,
}: Props) => {
  const { stripeId, name, price, currency, recurrence } = product;

  const background = useMemo(generateBackgroundColor, []);

  return (
    <li
      className={cn(
        'h-64 p-4 rounded-md cursor-pointer hover:scale-105 flex flex-col justify-between',
        {
          'cursor-wait opacity-50 pointer-events-none': !!disabled,
        },
      )}
      style={{ background }}
      onClick={() => onClick(stripeId)}
    >
      <h3 className="text-xl">{name}</h3>
      <div className="self-end">
        <span>{formatCurrency(currency)}</span>
        <span>{(price / 100.0).toFixed(2)}</span>
        {recurrence && <span>&nbsp;{formatRecurrence(recurrence)}</span>}
      </div>
    </li>
  );
};

export default CustomerProductCardComponent;
