import React, { FC } from 'react';
import LoginCard from '../components/LoginCard';
import { Helmet } from 'react-helmet';
import { ActionFunction, redirect } from 'react-router-dom';
import { createMerchant } from '../api-routes';

export const loginAction: ActionFunction = async ({ request }) => {
  const formData = await request.formData();
  const { type, email, businessName } = Object.fromEntries(formData);

  if (type === 'merchant') {
    const response = await fetch(createMerchant(), {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ email, businessName }),
    });

    if (response.status === 201) {
      response.headers.forEach((value, key) => console.log(`${key}: ${value}`));
      const merchantUrl = response.headers.get('Location');
      if (merchantUrl) {
        return redirect(merchantUrl);
      }
    }

    throw new Response('', { status: 500 });
  }
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
