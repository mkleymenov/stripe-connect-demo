import { FC } from 'react';
import { Helmet } from 'react-helmet';
import { LoaderFunction, useLoaderData } from 'react-router-dom';
import { getCustomer } from '../api-routes';
import GoToStripeButton from '../components/GoToStripeButton';

type Customer = {
  id: number;
  name: string;
};

export const customerLoader: LoaderFunction = async ({
  params,
}): Promise<Customer> => {
  if (params.id) {
    const response = await fetch(getCustomer(params.id), {
      headers: { 'Content-Type': 'application/json' },
    });
    if (response.ok) {
      const customer = (await response.json()) as Customer;
      return {
        id: customer.id,
        name: customer.name,
      };
    }
  }

  throw new Response('Merchant not found', { status: 404 });
};

const CustomerRoute: FC = () => {
  const { id, name } = useLoaderData() as Customer;

  return (
    <>
      <Helmet>
        <title>{name}</title>
      </Helmet>

      <h1 className="text-3xl text-grey-primary mb-4">Welcome, {name}!</h1>

      <div className="flex flex-row content-between items-center rounded-md px-4 py-4 bg-green-light">
        <div className="flex-auto">
          Go to Stripe to see your payment history and manage subscriptions
        </div>
        <div>
          <GoToStripeButton userId={id} type="billing_portal" />
        </div>
      </div>
    </>
  );
};

export default CustomerRoute;
