import React, { FC } from 'react';
import Page from './components/Page';
import LoginCard from './components/LoginCard';

const HomeComponent: FC = () => {
  return (
    <Page title="Home">
      <h1 className="mb-8 text-3xl text-center">
        [Some catchy phrase goes here]
      </h1>

      <ul className="flex flex-row gap-8 lg:gap-32 justify-center">
        <li>
          <LoginCard title="I am a customer" />
        </li>
        <li>
          <LoginCard title="I am a merchant" />
        </li>
      </ul>
    </Page>
  );
};

export default HomeComponent;
