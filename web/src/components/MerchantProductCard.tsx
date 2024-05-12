import { ChangeEventHandler, useCallback, useState } from 'react';
import { Form, useNavigation } from 'react-router-dom';
import cn from 'classnames';

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

  const { state, location } = useNavigation();

  const [isRecurring, setIsRecurring] = useState<boolean>(!!recurrence);

  const onRecurringChange: ChangeEventHandler<HTMLInputElement> = useCallback(
    (event) => {
      setIsRecurring(event.currentTarget.checked);
    },
    [],
  );

  return (
    <Form method="POST" className="border border-grey-secondary rounded-md p-4">
      <fieldset className="mb-4 flex items-center gap-4">
        <legend className="mb-2 font-medium">Price</legend>

        <input
          type="number"
          name="price"
          min="1"
          max="999999"
          step="0.01"
          defaultValue={formatPrice(price)}
          className="border rounded border-grey-secondary p-1"
        />

        <select
          name="currency"
          defaultValue={currency.toUpperCase()}
          className="border rounded border-grey-secondary p-1.5"
        >
          <option value="EUR">EUR</option>
          <option value="GBP">GBP</option>
          <option value="USD">USD</option>
        </select>

        <label className="flex items-center">
          <input
            type="checkbox"
            name="isRecurring"
            defaultChecked={isRecurring}
            onChange={onRecurringChange}
            className="mr-2 w-4 h-4"
          />
          <span className="font-medium">Repeat</span>
        </label>

        {isRecurring && (
          <>
            <label>
              Every
              <input
                type="number"
                name="intervalCount"
                min="1"
                max="12"
                defaultValue={recurrence?.intervalCount || 1}
                className="ml-2 border rounded border-grey-secondary p-1"
              />
            </label>

            <select
              name="interval"
              defaultValue={recurrence?.interval || 'MONTH'}
              className="border rounded border-grey-secondary p-1.5"
            >
              <option value="DAY">day(s)</option>
              <option value="WEEK">week(s)</option>
              <option value="MONTH">month(s)</option>
              <option value="YEAR">year(s)</option>
            </select>
          </>
        )}
      </fieldset>

      <div className="flex items-center gap-4">
        <input
          type="submit"
          className={cn(
            'px-4 py-2 cursor-pointer font-medium uppercase rounded text-accent bg-green-dark hover:bg-opacity-90 hover:border-opacity-90',
            { 'cursor-wait animate-pulse': state === 'submitting' },
          )}
          disabled={state === 'submitting'}
          value="Save"
        />

        <span
          className={cn('text-green-primary transition-opacity duration-200', {
            'delay-500 opacity-0': state === 'idle' || state === 'submitting',
            'opacity-100': state === 'loading' && !!location,
          })}
        >
          Saved
        </span>
      </div>
    </Form>
  );
};

export default MerchantProductCardComponent;
