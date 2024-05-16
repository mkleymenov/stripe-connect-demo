# Stripe Connect Demo for ITKonekt 2024

A simple marketplace web application that leverages [Stripe Connect](https://docs.stripe.com/connect) and [Stripe Billing](https://docs.stripe.com/billing) for onboarding merchants, collecting payments from customers, and routing funds from customers to merchants.
The application makes heavy use of Stripe low-code components and hosted experiences, such as:
- Stripe-hosted [Connect onboarding](https://docs.stripe.com/connect/hosted-onboarding)
- [Express Connect Dashboard](https://docs.stripe.com/connect/express-dashboard)
- [Connect Payments](https://docs.stripe.com/connect/supported-embedded-components/payments) embedded component
- Stripe-hosted [Checkout experience](https://docs.stripe.com/payments/checkout)
- Stripe-hosted [Customer Portal](https://docs.stripe.com/customer-management)

### Tech Stack

- Java 21
- Gradle
- Spring Boot
- H2 database (local)
- TypeScript
- React.js (create-react-app)

## Build and Run

### Server

**Build with Gradle**
```shell
./gradlew build
```

**Run with Gradle**

You'll have to manually export environment variables from the [server/.env](./server/.env) file because Gradle does not support loading environment variables from files. 
See the [Stripe Account Setup and API Keys](#stripe-account-setup-and-api-keys) section for instructions on how to get API keys.

```shell
STRIPE_API_KEY=sk_test_XXX STRIPE_WEBHOOK_SECRET=whsec_test_XXX ./gradlew bootRun
```

**Run with IntelliJ**

Create a new Spring Boot run configuration. Set the main class to `com.dataart.itkonekt.App`. 

In the _Build and Run_ section select _Modify options... -> Environment variables_.
Specify the full path to the [server/.env](./server/.env) file in the _Environment variables_ input.

The final configuration should look as follows:
<img width="1083" alt="image" src="https://github.com/mkleymenov/stripe-connect-demo/assets/1931235/e5101038-7e7d-4096-a3ac-91d99c40bd9e">


### Client

All commands below should be run from the [web](./web) directory.

**Install dependencies**
```shell
npm install
```

**Build**
```shell
npm run build
```

**Run**

Note: React scripts will automatically read environment variables from `.env` or `.env.local` files.
See the [Stripe Account Setup and API Keys](#stripe-account-setup-and-api-keys) section for instructions on how to get API keys

```shell
npm run start
```

## Application Overview

The web application consists of three pages:
- A Home page with customer and merchant registration and quick login links.

  <img width="576" alt="image" src="https://github.com/mkleymenov/stripe-connect-demo/assets/1931235/aae6514e-eecf-414e-a564-c4871519a66c">
- A Merchant page with a Connect account onboarding / dashboard link, a merchant product configuration panel, and a list of payments received from customers.

  <img width="547" alt="image" src="https://github.com/mkleymenov/stripe-connect-demo/assets/1931235/acc3d337-4196-4857-a3a0-a3d08f4958d7">
- A Customer page with a Customer Portal link and a catalogue of products offered by merchants.

  <img width="556" alt="image" src="https://github.com/mkleymenov/stripe-connect-demo/assets/1931235/010d8432-99b3-4b0c-acca-2b966bbe9f84">

A typical user journey looks like follows:
1. Create a new Merchant account
2. Complete the hosted Connect Onboarding flow
3. Create a Product
4. Create a new Customer account
5. Buy one or more products
6. Check out Customer Portal to view and manage subscriptions, invoices and billing details.
7. Log in as the Merchant from whom a product was purchased
8. Check out the list of payments
9. Log in to the Express Connect Dashboard, check out received payments.

### Test Data

The application is intended to be used with a Stripe account [Test mode](https://docs.stripe.com/test-mode#test-mode) API keys. 
This means that all Stripe features are behaving identically to the Live mode, but no real charges take place, in particular
because real cards / bank accounts cannot be used in the Test mode. Instead, Stripe provides a set of test
data for [payments](https://docs.stripe.com/testing) and [Connect accounts](https://docs.stripe.com/connect/testing) that can
be used to simulate both success and error scenarios.

For example, in order to make a successful purchase of a merchant product, one can use a test Visa card with number `4242424242424242`, any valid expiration date, and any 3-digit CVC.

## Stripe Account Setup and API Keys

The application assumes that a user has a Stripe account with Connect product enabled in a Test mode. In order to set up the account:
- Create a new Stripe account at https://stripe.com.
- Log in to the [Stripe Dashboard](https://dashboard.stripe.com/). Switch to the Test mode (a toggle in the upper right corner).
- Go to the [Connect](https://dashboard.stripe.com/test/connect) section and go through the Connect activation wizard.
  * The application is intended to be used with [Express Connect](https://docs.stripe.com/connect/express-accounts) accounts (connected accounts are created by the platform, onboarding is hosted by Stripe)
    and [Destination Charges](https://docs.stripe.com/connect/destination-charges) (customers transact with the platform which then forwards payments to connected accounts).
  *  Make sure to accept the responsibilities imposed on your account by the selected Connect integration method.
- Go to the [Customer Portal settings](https://dashboard.stripe.com/test/settings/billing/portal) and click 'Save' to save the current configuration (also feel free to customize the configuration).
- [Optional] Customize branding for [your account](https://dashboard.stripe.com/settings/branding) and your [connected accounts](https://dashboard.stripe.com/settings/connect/onboarding-interface). You may need to temporary switch to the Live mode in order to get access to all branding options.
- [Optional] Customize branding for your connected accounts [Express Dashboard](https://dashboard.stripe.com/settings/connect/express-dashboard/branding).
- [Optional] Customize [payment methods](https://dashboard.stripe.com/test/settings/payment_methods) available for customers on Stripe Checkout.

After your Stripe account is set up, go to the [Developers -> API keys](https://dashboard.stripe.com/test/apikeys) page to get a publishable and a secret API keys for your account (make sure you're in the Test mode!).

- Set the `STRIPE_API_KEY` environment variable in [server/.env](./server/.env) file to the **secret** API key.
- Set the `REACT_APP_STRIPE_PUBLISHABLE_KEY` environment variable in [web/.env](./web/.env) to the **publishable** API key

### Stripe Webhooks

You can use [Stripe CLI](https://docs.stripe.com/stripe-cli/overview) to forward any webhook events from your Stripe account to the local server.

```shell
stripe listen --forward-to http://localhost:8080/webhook
```

Copy the webhook signing secret (`whsec_XXX`) printed by the Stripe CLI and set it as a value for
the `STRIPE_WEBHOOK_SECRET` environment variable in [server/.env](./server/.env) file. If the server
is running, restart it to apply the changes.

---
Made with :heart: by Mikhail Kleymenov
