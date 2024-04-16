const API_BASE_URL = 'http://localhost:8080';

export const createMerchant = (): string => `${API_BASE_URL}/merchant`;

export const getMerchant = (merchantId: string): string =>
  `${API_BASE_URL}/merchant/${merchantId}`;

export const createOnboardingLink = (merchantId: string): string =>
  `${API_BASE_URL}/merchant/${merchantId}/onboarding`;

export const createDashboardLink = (merchantId: string): string =>
  `${API_BASE_URL}/merchant/${merchantId}/dashboard`;

export const createSession = (merchantId: string): string =>
  `${API_BASE_URL}/merchant/${merchantId}/session`;
