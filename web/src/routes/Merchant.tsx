import { FC } from 'react';
import { Helmet } from 'react-helmet';
import { LoaderFunction, useLoaderData } from 'react-router-dom';
import { getMerchant } from '../api-routes';
import GoToStripeButton from '../components/GoToStripeButton';

type Merchant = {
  id: number;
  businessName: string;
  status: 'REJECTED' | 'PENDING' | 'IN_REVIEW' | 'ACTIVE';
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

const MerchantAccountRejected = () => (
  <div>
    <h2>Your merchant account has been blocked.</h2>
    <p>
      Sorry, but your business does not meet our eligibility criteria. Please
      reach out to the customer support for details.
    </p>
  </div>
);

const MerchantAccountPending = ({ merchantId }: { merchantId: number }) => (
  <div>
    <h2>We need more information about your business.</h2>
    <p>
      Please go to Stripe to provide additional information about your business.
    </p>
    <p>
      <GoToStripeButton merchantId={merchantId} type="onboarding" />
    </p>
  </div>
);

const MerchantAccountInReview = () => (
  <div>
    <h2>Your account is in review.</h2>
    <p>
      We are reviewing your business information. Once the review is completed,
      you will be able to receive payments.
    </p>
  </div>
);

const MerchantAccountActive = ({ merchantId }: { merchantId: number }) => (
  <div>
    <h2>You are fully set up!</h2>
    <p>
      Go to Stripe to see payments you received and edit information about your
      business.
    </p>
    <p>
      <GoToStripeButton merchantId={merchantId} type={'dashboard'} />
    </p>
  </div>
);

const MerchantRoute: FC = () => {
  const { id, businessName, status } = useLoaderData() as Merchant;

  return (
    <>
      <Helmet>
        <title>{businessName}</title>
      </Helmet>

      <h1>{businessName}</h1>

      {status === 'REJECTED' && <MerchantAccountRejected />}
      {status === 'PENDING' && <MerchantAccountPending merchantId={id} />}
      {status === 'IN_REVIEW' && <MerchantAccountInReview />}
      {status === 'ACTIVE' && <MerchantAccountActive merchantId={id} />}
    </>
  );
};

export default MerchantRoute;
