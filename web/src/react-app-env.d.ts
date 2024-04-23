/// <reference types="react-scripts" />

type Customer = {
  id: number;
  name: string;
};

type MerchantStatus = 'REJECTED' | 'PENDING' | 'IN_REVIEW' | 'ACTIVE';

type Merchant = {
  id: number;
  businessName: string;
  status: MerchantStatus;
};

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
