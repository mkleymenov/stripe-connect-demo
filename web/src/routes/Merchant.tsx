import { FC } from 'react';
import { Helmet } from 'react-helmet';
import {
  ActionFunction,
  LoaderFunction,
  useLoaderData,
} from 'react-router-dom';
import {
  createProduct,
  createSession,
  getMerchant,
  getProducts,
} from '../api-routes';
import { Props as MerchantStatusCardProps } from '../components/MerchantStatusCard';
import MerchantProductCard, {
  parseFormData,
} from '../components/MerchantProductCard';

type MerchantLoaderData = {
  merchant: Merchant;
  product?: Product;
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

export const merchantLoader: LoaderFunction = async ({
  params,
}): Promise<MerchantLoaderData> => {
  if (params.id) {
    const [merchantResponse, productsResponse] = await Promise.all([
      fetch(getMerchant(params.id), {
        headers: { 'Content-Type': 'application/json' },
      }),
      fetch(getProducts(params.id), {
        headers: { 'Content-Type': 'application/json' },
      }),
    ]);

    if (merchantResponse.ok && productsResponse.ok) {
      const [merchant, products] = await Promise.all([
        merchantResponse.json() as Promise<Merchant>,
        productsResponse.json() as Promise<Product[]>,
      ]);
      const [product] = products;

      return {
        merchant: {
          id: merchant.id,
          businessName: merchant.businessName,
          status: merchant.status,
        },
        product,
      };
    }
  }

  throw new Response('Merchant not found', { status: 404 });
};

export const merchantAction: ActionFunction = async ({ params, request }) => {
  const merchantId = params.id || '';
  const formData = await request.formData();
  const payload = parseFormData(formData);

  const response = await fetch(createProduct(), {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      merchantId,
      ...payload,
    }),
  });

  if (response.ok) {
    return response.json();
  }

  throw new Response('Sorry, something went wrong', { status: 500 });
};

const fetchConnectClientSecret = async (merchantId: number) => {
  const response = await fetch(createSession(merchantId.toString()), {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
  });
  if (response.ok) {
    return await response.text();
  }
  return '';
};

const MerchantRoute: FC = () => {
  const { merchant, product } = useLoaderData() as MerchantLoaderData;
  const { id, businessName, status } = merchant;
  const { title, subtitle, style } = MERCHANT_STATUS_CARDS[status];

  // TODO: initialize Stripe Connect JS instance

  return (
    <>
      <Helmet>
        <title>{businessName}</title>
      </Helmet>

      <h1 className="text-3xl text-grey-primary mb-8">{businessName}</h1>

      {/* TODO: merchant status card */}
      {/*<MerchantStatusCard*/}
      {/*  title={title}*/}
      {/*  subtitle={subtitle}*/}
      {/*  style={style}*/}
      {/*  className="mb-8"*/}
      {/*>*/}
      {/*  {status === 'PENDING' && (*/}
      {/*    <GoToStripeButton userId={id} type="onboarding" />*/}
      {/*  )}*/}
      {/*  {status === 'ACTIVE' && (*/}
      {/*    <GoToStripeButton userId={id} type="dashboard" />*/}
      {/*  )}*/}
      {/*</MerchantStatusCard>*/}

      <div className="mb-8">
        <h2 className="text-2xl font-medium mb-2">Configure Your Product</h2>
        <MerchantProductCard product={product} />
      </div>

      {/* TODO: Connect Payments component */}
      <div className="mb-8">
        <h2 className="text-2xl font-medium mb-2">Payments</h2>
      </div>
    </>
  );
};

export default MerchantRoute;
