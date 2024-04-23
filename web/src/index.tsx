import React from 'react';
import ReactDOM from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import './index.css';
import Customer, { customerLoader } from './routes/Customer';
import Merchant, { merchantAction, merchantLoader } from './routes/Merchant';
import LayoutRoute from './routes/Layout';
import Home, { homeLoader, signUpAction } from './routes/Home';
import Error from './routes/Error';

const router = createBrowserRouter([
  {
    element: <LayoutRoute />,
    errorElement: <Error />,
    children: [
      {
        path: '/',
        element: <Home />,
        loader: homeLoader,
        action: signUpAction,
      },
      {
        path: '/merchant/:id',
        element: <Merchant />,
        loader: merchantLoader,
        action: merchantAction,
      },
      { path: '/customer/:id', element: <Customer />, loader: customerLoader },
    ],
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
