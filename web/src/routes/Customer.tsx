import { FC, useCallback, useState } from 'react';
import { Helmet } from 'react-helmet';
import { LoaderFunction, useLoaderData } from 'react-router-dom';
import { createCheckoutSession, getCustomer, getProducts } from '../api-routes';
import GoToStripeButton from '../components/GoToStripeButton';
import ProductCard from '../components/CustomerProductCard';

type CustomerLoaderData = {
  customer: Customer;
  products: Product[];
};

export const customerLoader: LoaderFunction = async ({
  params,
}): Promise<CustomerLoaderData> => {
  if (params.id) {
    const [customerResponse, productsResponse] = await Promise.all([
      fetch(getCustomer(params.id), {
        headers: { 'Content-Type': 'application/json' },
      }),
      fetch(getProducts(), {
        headers: { 'Content-Type': 'application/json' },
      }),
    ]);

    if (customerResponse.ok && productsResponse.ok) {
      const [customer, products] = await Promise.all([
        customerResponse.json() as Promise<Customer>,
        productsResponse.json() as Promise<Product[]>,
      ]);
      return {
        customer: {
          id: customer.id,
          name: customer.name,
        },
        products,
      };
    }
  }

  throw new Response('Customer not found', { status: 404 });
};

const CustomerRoute: FC = () => {
  const { customer, products } = useLoaderData() as CustomerLoaderData;

  const [isLoading, setIsLoading] = useState(false);

  const onProductClick = useCallback(
    async (stripePriceId: string) => {
      setIsLoading(true);

      const response = await fetch(
        createCheckoutSession(customer.id.toString()),
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ stripePriceId }),
        },
      );

      if (response.ok) {
        const redirectUrl = response.headers.get('Location');
        if (redirectUrl) {
          window.location.href = redirectUrl;
          return;
        }
      }

      setIsLoading(false);
    },
    [customer.id],
  );

  return (
    <>
      <Helmet>
        <title>{customer.name}</title>
      </Helmet>

      <h1 className="text-3xl text-grey-primary mb-8">
        Welcome, {customer.name}!
      </h1>

      <div className="flex flex-row content-between items-center rounded-md p-4 mb-8 bg-green-light">
        <div className="flex-auto">
          Go to Stripe to see your payment history and manage subscriptions
        </div>
        <div>
          <GoToStripeButton userId={customer.id} type="billing_portal" />
        </div>
      </div>

      <div className="mb-8">
        <h2 className="text-2xl font-medium mb-4">Explore products</h2>

        <ul className="grid grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3 md:gap-8 justify-between">
          {products.map((product) => (
            <ProductCard
              key={product.stripeId}
              product={product}
              onClick={onProductClick}
              disabled={isLoading}
            />
          ))}
        </ul>
      </div>
    </>
  );
};

export default CustomerRoute;
