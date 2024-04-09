import React, { ReactNode } from 'react';
import { Helmet } from 'react-helmet';
import Header from './Header';

type Props = {
  children: ReactNode;
  title: string;
};

const PageComponent = ({ children, title }: Props) => {
  return (
    <main className="flex flex-col">
      <Helmet>
        <title>{title}</title>
      </Helmet>

      <Header />

      <section className="w-full lg:w-2/3 mx-auto my-16">{children}</section>
    </main>
  );
};

export default PageComponent;
