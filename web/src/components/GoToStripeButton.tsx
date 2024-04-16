import { useCallback, useState } from 'react';
import cn from 'classnames';
import { createDashboardLink, createOnboardingLink } from '../api-routes';

type Props = {
  merchantId: number;
  type: 'onboarding' | 'dashboard';
};

const GoToStripeButton = ({ merchantId, type }: Props) => {
  const [submitted, setSubmitted] = useState(false);

  const onClick = useCallback(async () => {
    if (!submitted) {
      setSubmitted(true);

      const url =
        type === 'onboarding'
          ? createOnboardingLink(merchantId.toString())
          : createDashboardLink(merchantId.toString());

      try {
        const response = await fetch(url, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
        });
        if (response.ok) {
          window.location.href = await response.text();
        }
      } finally {
        setSubmitted(false);
      }
    }
  }, [merchantId, setSubmitted, submitted, type]);

  return (
    <button
      type="button"
      onClick={onClick}
      className={cn(
        'p-2 cursor-pointer font-medium uppercase rounded text-accent bg-green-dark hover:bg-opacity-90 hover:border-opacity-90',
        {
          'cursor-wait': submitted,
        },
      )}
      disabled={submitted}
    >
      Go to Stripe
    </button>
  );
};

export default GoToStripeButton;
