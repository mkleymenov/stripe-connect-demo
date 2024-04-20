type Recurrence = {
  interval: 'DAY' | 'WEEK' | 'MONTH' | 'YEAR';
  intervalCount: number;
};

type Props = {
  product: Product;
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

const ProductCardComponent = ({ product }: Props) => {
  const { name, price, currency, recurrence } = product;

  return (
    <div>
      <h3>{name}</h3>
      <div>
        <span>{(price / 100.0).toFixed(2)}</span>
        <span>{currency.toUpperCase()}</span>
        {recurrence && <span>{formatRecurrence(recurrence)}</span>}
      </div>
    </div>
  );
};

export default ProductCardComponent;
