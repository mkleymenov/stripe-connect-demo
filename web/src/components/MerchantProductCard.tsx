import { ChangeEventHandler, useCallback, useState } from 'react';
import { Form } from 'react-router-dom';

type Props = {
  product?: Product;
};

type ProductPayload = {
  price: number;
  currency: string;
  recurrence?: ProductRecurrence;
};

const formatPrice = (price: number): string => `${(price / 100.0).toFixed(2)}`;

export const parseFormData = (formData: FormData): ProductPayload => {
  const { price, currency, interval, intervalCount } =
    Object.fromEntries(formData);

  const recurrence: ProductRecurrence | undefined =
    interval && intervalCount
      ? {
          interval: interval as BillingInterval,
          intervalCount: parseInt(intervalCount as string),
        }
      : undefined;

  return {
    price: Math.floor(parseFloat(price as string) * 100),
    currency: currency as string,
    recurrence,
  };
};

const MerchantProductCardComponent = ({ product }: Props) => {
  const { price = 0, recurrence, currency = 'EUR' } = product || {};

  const [isRecurring, setIsRecurring] = useState<boolean>(!!recurrence);

  const onRecurringChange: ChangeEventHandler<HTMLInputElement> = useCallback(
    (event) => {
      setIsRecurring(event.currentTarget.checked);
    },
    [],
  );

  return (
    <Form method="POST">
      <label>
        Price
        <input
          type="number"
          name="price"
          min="1"
          defaultValue={formatPrice(price)}
        />
      </label>

      <label>
        Currency
        <select name="currency">
          <option value="EUR" selected={currency === 'EUR'}>
            EUR
          </option>
          <option value="GBP" selected={currency === 'GBP'}>
            GBP
          </option>
          <option value="USD" selected={currency === 'USD'}>
            USD
          </option>
        </select>
      </label>

      <label>
        Set up recurring charges
        <input
          type="checkbox"
          name="isRecurring"
          defaultChecked={isRecurring}
          onChange={onRecurringChange}
        />
      </label>

      {isRecurring && (
        <label>
          Repeat every
          <fieldset>
            <input
              type="number"
              name="intervalCount"
              min="1"
              defaultValue={recurrence?.intervalCount || 1}
            />

            <select name="interval">
              <option value="DAY" selected={recurrence?.interval === 'DAY'}>
                day(s)
              </option>
              <option value="WEEK" selected={recurrence?.interval === 'WEEK'}>
                week(s)
              </option>
              <option value="MONTH" selected={recurrence?.interval === 'MONTH'}>
                month(s)
              </option>
              <option value="YEAR" selected={recurrence?.interval === 'YEAR'}>
                year(s)
              </option>
            </select>
          </fieldset>
        </label>
      )}

      <input type="submit" value="Save" />
    </Form>
  );
};

export default MerchantProductCardComponent;
