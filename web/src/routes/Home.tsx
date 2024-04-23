import React, { FC } from 'react';
import SignUpCard from '../components/SignUpCard';
import { Helmet } from 'react-helmet';
import {
  ActionFunction,
  LoaderFunction,
  redirect,
  useLoaderData,
} from 'react-router-dom';
import {
  createCustomer,
  createMerchant,
  listCustomers,
  listMerchants,
} from '../api-routes';
import LoginCard from '../components/LoginCard';

type HomeLoaderData = {
  customers: Customer[];
  merchants: Merchant[];
};

export const homeLoader: LoaderFunction = async (): Promise<HomeLoaderData> => {
  const [customersResponse, merchantsResponse] = await Promise.all([
    fetch(listCustomers(), {
      headers: {
        'Content-Type': 'application/json',
      },
    }),
    fetch(listMerchants(), {
      headers: {
        'Content-Type': 'application/json',
      },
    }),
  ]);

  if (customersResponse.ok && merchantsResponse.ok) {
    const [customers, merchants] = await Promise.all([
      customersResponse.json(),
      merchantsResponse.json(),
    ]);

    return { customers, merchants };
  }

  throw new Response('An unexpected error has occurred', { status: 500 });
};

export const signUpAction: ActionFunction = async ({ request }) => {
  const formData = await request.formData();
  const { type, name, email, businessName } = Object.fromEntries(formData);

  const url = type === 'customer' ? createCustomer() : createMerchant();
  const payload =
    type === 'customer' ? { name, email } : { businessName, email };

  const response = await fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  });

  if (response.status === 201) {
    const redirectUrl = response.headers.get('Location');
    if (redirectUrl) {
      return redirect(redirectUrl);
    }
  }

  throw new Response('', { status: 500 });
};

const HomeRoute: FC = () => {
  const { customers, merchants } = useLoaderData() as HomeLoaderData;

  return (
    <>
      <Helmet>
        <title>Stripe Connect Demo</title>
      </Helmet>

      <h1 className="mb-8 text-3xl text-center">Stripe Connect Demo</h1>

      <div className="flex flex-row gap-8 lg:gap-32 justify-center">
        <div>
          <SignUpCard type="customer" title="Create Customer" />

          {customers?.length && (
            <h2 className="text-xl font-medium text-center my-8">
              Log in with
            </h2>
          )}

          {customers?.length && (
            <ul className="my-8">
              {customers.map(({ id, name }) => (
                <li key={id} className="mb-8 last:mb-0">
                  <LoginCard type="customer" id={id} title={name} />
                </li>
              ))}
            </ul>
          )}
        </div>

        <div>
          <SignUpCard type="merchant" title="Create Merchant" />

          {merchants?.length && (
            <h2 className="text-xl font-medium text-center my-8">
              Log in with
            </h2>
          )}

          {merchants?.length && (
            <ul className="my-8">
              {merchants.map(({ id, businessName }) => (
                <li key={id} className="mb-8 last:mb-0">
                  <LoginCard type="merchant" id={id} title={businessName} />
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </>
  );
};

export default HomeRoute;
