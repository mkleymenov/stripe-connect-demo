import { FC, useCallback } from 'react';
import { Helmet } from 'react-helmet';
import { LoaderFunction, useLoaderData } from 'react-router-dom';
import { getCustomer, getProducts } from '../api-routes';
import GoToStripeButton from '../components/GoToStripeButton';
import ProductCard from '../components/ProductCard';

type Customer = {
  id: number;
  name: string;
};

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

  const onProductClick = useCallback(
    (stripeId: string) => {
      console.log(`Customer ${customer.id} selected product ${stripeId}`);
    },
    [customer.id],
  );

  return (
    <>
      <Helmet>
        <title>{customer.name}</title>
      </Helmet>

      <h1 className="text-3xl text-grey-primary mb-4">
        Welcome, {customer.name}!
      </h1>

      <div className="flex flex-row content-between items-center rounded-md px-4 py-4 bg-green-light">
        <div className="flex-auto">
          Go to Stripe to see your payment history and manage subscriptions
        </div>
        <div>
          <GoToStripeButton userId={customer.id} type="billing_portal" />
        </div>
      </div>

      <h2 className="text-2xl text-grey-secondary mb-4">Explore products</h2>

      <ul>
        {products.map((product) => (
          <li
            key={product.stripeId}
            onClick={() => onProductClick(product.stripeId)}
          >
            <ProductCard product={product} />
          </li>
        ))}
      </ul>
    </>
  );
};

export default CustomerRoute;
