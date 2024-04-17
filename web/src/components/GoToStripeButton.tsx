import { useCallback, useMemo, useState } from 'react';
import cn from 'classnames';
import {
  createBillingPortalLink,
  createDashboardLink,
  createOnboardingLink,
} from '../api-routes';

type Props = {
  userId: number;
  type: 'onboarding' | 'dashboard' | 'billing_portal';
};

const GoToStripeButton = ({ userId, type }: Props) => {
  const [submitted, setSubmitted] = useState(false);

  const url = useMemo(() => {
    const id = userId.toString();
    switch (type) {
      case 'onboarding':
        return createOnboardingLink(id);
      case 'dashboard':
        return createDashboardLink(id);
      case 'billing_portal':
        return createBillingPortalLink(id);
    }
  }, [userId, type]);

  const onClick = useCallback(async () => {
    if (!submitted) {
      setSubmitted(true);

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
  }, [setSubmitted, submitted]);

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
