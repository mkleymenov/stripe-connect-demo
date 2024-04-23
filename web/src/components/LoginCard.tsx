import { useMemo } from 'react';
import { Link } from 'react-router-dom';

type Props = {
  type: 'customer' | 'merchant';
  id: number;
  title: string;
};

const generateBackgroundColor = () => {
  const hue = Math.floor(Math.random() * 360);
  return `hsl(${hue}deg, 100%, 90%)`;
};

const LoginCard = ({ type, id, title }: Props) => {
  const background = useMemo(generateBackgroundColor, []);

  const path = useMemo(() => {
    switch (type) {
      case 'customer':
        return `/customer/${id}`;
      case 'merchant':
        return `/merchant/${id}`;
    }
  }, [type, id]);

  return (
    <Link
      to={path}
      className="block p-4 rounded-md text-center cursor-pointer"
      style={{ background }}
    >
      {title}
    </Link>
  );
};

export default LoginCard;
