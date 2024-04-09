import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import Home from './Home';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import Customer from './customer/Customer';
import Merchant from './merchant/Merchant';

const router = createBrowserRouter([
  {
    path: '/',
    element: <Home />,
  },
  {
    path: 'customer',
    element: <Customer />,
  },
  {
    path: 'merchant',
    element: <Merchant />,
  },
]);

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement,
);
root.render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>,
);
