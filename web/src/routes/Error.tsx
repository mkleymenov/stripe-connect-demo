import { Helmet } from 'react-helmet';
import { isRouteErrorResponse, useRouteError } from 'react-router-dom';

const getErrorTitle = (error: unknown): string => {
  if (isRouteErrorResponse(error)) {
    if (error.status === 404) {
      return 'Not found';
    }
  }

  return 'Error';
};

const getErrorMessage = (error: unknown): string => {
  if (isRouteErrorResponse(error)) {
    // If an error has a custom message, use it
    const { message } = error.data || {};
    if (message) {
      return message;
    }

    // Return a generic message matching the error status code
    if (error.status === 404) {
      return 'The requested resource does not exist.';
    }
  }

  return 'Something went wrong. Please try again.';
};

const ErrorRoute = () => {
  const error = useRouteError();

  return (
    <>
      <Helmet>
        <title>{getErrorTitle(error)}</title>
      </Helmet>

      <div className="text-center">
        <h1 className="text-grey-primary font-bold text-xl">Oops!</h1>
        <h2 className="text-grey-secondary">{getErrorMessage(error)}</h2>
      </div>
    </>
  );
};

export default ErrorRoute;
