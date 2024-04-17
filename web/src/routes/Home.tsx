import React, { FC } from 'react';
import LoginCard from '../components/LoginCard';
import { Helmet } from 'react-helmet';
import { ActionFunction, redirect } from 'react-router-dom';
import { createCustomer, createMerchant } from '../api-routes';

export const loginAction: ActionFunction = async ({ request }) => {
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
  return (
    <>
      <Helmet>
        <title>Home</title>
      </Helmet>

      <h1 className="mb-8 text-3xl text-center">
        [Some catchy phrase goes here]
      </h1>

      <ul className="flex flex-row gap-8 lg:gap-32 justify-center">
        <li>
          <LoginCard type="customer" title="I am a customer" />
        </li>
        <li>
          <LoginCard type="merchant" title="I am a merchant" />
        </li>
      </ul>
    </>
  );
};

export default HomeRoute;
