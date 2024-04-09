import {
  ChangeEventHandler,
  MouseEventHandler,
  useCallback,
  useState,
} from 'react';

type Props = {
  title: string;
};

const LoginCardComponent = ({ title }: Props) => {
  const [email, setEmail] = useState('');

  const onEmailChange: ChangeEventHandler<HTMLInputElement> = useCallback(
    (e) => {
      setEmail(e.currentTarget.value);
    },
    [setEmail],
  );

  const onSubmit: MouseEventHandler<HTMLInputElement> = useCallback(
    (e) => {
      e.preventDefault();
    },
    [email],
  );

  return (
    <div className="w-96 p-8 bg-green-light rounded-lg flex flex-col gap-4">
      <h2 className="text-xl text-grey-primary text-center font-medium">
        {title}
      </h2>

      <form action="#" className="flex flex-col justify-center gap-4">
        <input
          className="rounded px-1 py-2 text-center border border-green-dark"
          name="email"
          type="email"
          placeholder="Email"
          required
          value={email}
          onChange={onEmailChange}
          onSubmit={onSubmit}
        />

        <input
          className="p-2 cursor-pointer font-medium uppercase border border-green-dark rounded text-accent bg-green-dark hover:bg-opacity-90 hover:border-opacity-90"
          type="submit"
          value="Log In"
          onClick={onSubmit}
        />
      </form>
    </div>
  );
};

export default LoginCardComponent;
