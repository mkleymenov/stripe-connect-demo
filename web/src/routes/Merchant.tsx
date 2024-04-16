import { loadConnectAndInitialize } from '@stripe/connect-js';
import {
  ConnectComponentsProvider,
  ConnectPayments,
} from '@stripe/react-connect-js';
import { FC, useState } from 'react';
import { Helmet } from 'react-helmet';
import { LoaderFunction, useLoaderData } from 'react-router-dom';
import { createSession, getMerchant } from '../api-routes';
import GoToStripeButton from '../components/GoToStripeButton';
import MerchantStatusCard, {
  Props as MerchantStatusCardProps,
} from '../components/MerchantStatusCard';

type MerchantStatus = 'REJECTED' | 'PENDING' | 'IN_REVIEW' | 'ACTIVE';

type Merchant = {
  id: number;
  businessName: string;
  status: MerchantStatus;
};

const MERCHANT_STATUS_CARDS: Record<MerchantStatus, MerchantStatusCardProps> = {
  ACTIVE: {
    title: 'You are fully set up!',
    subtitle: 'Go to Stripe to manage your merchant account.',
    style: 'success',
  },
  IN_REVIEW: {
    title: 'Your account is in review.',
    subtitle:
      'We are reviewing your business information. Once the review is completed, you will be able to receive payments.',
    style: 'info',
  },
  PENDING: {
    title: 'We need more information about your business.',
    subtitle:
      'Please go to Stripe to provide additional information about your business.',
    style: 'info',
  },
  REJECTED: {
    title: 'Your merchant account has been blocked.',
    subtitle:
      'Sorry, but your business does not meet our eligibility criteria. Please reach out to the customer support for details.',
    style: 'error',
  },
};

export const merchantLoader: LoaderFunction = async ({ params }) => {
  if (params.id) {
    const response = await fetch(getMerchant(params.id), {
      headers: { 'Content-Type': 'application/json' },
    });
    if (response.ok) {
      const merchant = (await response.json()) as Merchant;
      return {
        id: merchant.id,
        businessName: merchant.businessName,
        status: merchant.status,
      };
    }
  }

  throw new Response('Merchant not found', { status: 404 });
};

const MerchantRoute: FC = () => {
  const { id, businessName, status } = useLoaderData() as Merchant;
  const { title, subtitle, style } = MERCHANT_STATUS_CARDS[status];

  const [stripeConnect] = useState(() => {
    const fetchSecret = async () => {
      const response = await fetch(createSession(id.toString()), {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
      });
      if (response.ok) {
        return await response.text();
      }
      return '';
    };

    return loadConnectAndInitialize({
      publishableKey: process.env.REACT_APP_STRIPE_PUBLISHABLE_KEY || '',
      fetchClientSecret: fetchSecret,
    });
  });

  return (
    <>
      <Helmet>
        <title>{businessName}</title>
      </Helmet>

      <h1 className="text-3xl text-grey-primary mb-4">{businessName}</h1>

      <ConnectComponentsProvider connectInstance={stripeConnect}>
        <MerchantStatusCard
          title={title}
          subtitle={subtitle}
          style={style}
          className="my-2"
        >
          {status === 'PENDING' && (
            <GoToStripeButton merchantId={id} type="onboarding" />
          )}
          {status === 'ACTIVE' && (
            <GoToStripeButton merchantId={id} type="dashboard" />
          )}
        </MerchantStatusCard>

        {['PENDING', 'ACTIVE'].includes(status) && (
          <div>
            <h2 className="text-2xl font-medium">Payments</h2>
            <ConnectPayments />
          </div>
        )}
      </ConnectComponentsProvider>
    </>
  );
};

export default MerchantRoute;
