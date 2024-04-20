/// <reference types="react-scripts" />

type BillingInterval = 'DAY' | 'WEEK' | 'MONTH' | 'YEAR';

type ProductRecurrence = {
  interval: BillingInterval;
  intervalCount: number;
};

type Product = {
  stripeId: string;
  name: string;
  price: number;
  currency: string;
  recurrence?: ProductRecurrence;
};
