import { Form } from 'react-router-dom';

type Props = {
  title: string;
  type: 'customer' | 'merchant';
};

const LoginCardComponent = ({ title, type }: Props) => (
  <div className="w-96 p-8 bg-green-light rounded-lg flex flex-col gap-4">
    <h2 className="text-xl text-grey-primary text-center font-medium">
      {title}
    </h2>

    <Form method="POST" className="flex flex-col justify-center gap-4">
      {type === 'customer' && (
        <input
          className="rounded px-1 py-2 text-center border border-green-dark"
          name="name"
          type="text"
          placeholder="Full Name"
          required
        />
      )}

      {type === 'merchant' && (
        <input
          className="rounded px-1 py-2 text-center border border-green-dark"
          name="businessName"
          type="text"
          placeholder="Business Name"
          required
        />
      )}

      <input
        className="rounded px-1 py-2 text-center border border-green-dark"
        name="email"
        type="email"
        placeholder="Email"
        required
      />

      <input name="type" type="hidden" defaultValue={type} />

      <input
        className="p-2 cursor-pointer font-medium uppercase border border-green-dark rounded text-accent bg-green-dark hover:bg-opacity-90 hover:border-opacity-90"
        type="submit"
        value="Log In"
      />
    </Form>
  </div>
);

export default LoginCardComponent;
