const API_BASE_URL = 'http://localhost:8080';

export const createMerchant = (): string => `${API_BASE_URL}/merchants`;

export const getMerchant = (merchantId: string): string =>
  `${API_BASE_URL}/merchants/${merchantId}`;

export const createOnboardingLink = (merchantId: string): string =>
  `${API_BASE_URL}/merchants/${merchantId}/onboarding`;

export const createDashboardLink = (merchantId: string): string =>
  `${API_BASE_URL}/merchants/${merchantId}/dashboard`;

export const createSession = (merchantId: string): string =>
  `${API_BASE_URL}/merchants/${merchantId}/session`;

export const createCustomer = (): string => `${API_BASE_URL}/customers`;

export const getCustomer = (customerId: string): string =>
  `${API_BASE_URL}/customers/${customerId}`;

export const createBillingPortalLink = (customerId: string): string =>
  `${API_BASE_URL}/customers/${customerId}/portal`;

export const getProducts = (merchantId?: string): string => {
  const search = new URLSearchParams();
  if (merchantId) {
    search.append('merchantId', merchantId);
  }

  return `${API_BASE_URL}/products?${search.toString()}`;
};

export const createProduct = (): string => `${API_BASE_URL}/products`;
