import React from 'react';
import { Outlet } from 'react-router-dom';
import Header from '../components/Header';

const LayoutRoute = () => {
  return (
    <main className="flex flex-col">
      <Header />

      <section className="w-full lg:w-2/3 mx-auto mt-8">
        <Outlet />
      </section>
    </main>
  );
};

export default LayoutRoute;
