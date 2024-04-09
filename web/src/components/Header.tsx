import { Link } from 'react-router-dom';

const HeaderComponent = () => (
  <header className="flex p-2 h-20">
    <img alt="Logo" />
    <nav className="ml-10 flex flex-col justify-center flex-grow">
      <ul className="flex gap-10">
        <li className="text-lg font-medium">
          <Link to="/" className="cursor-pointer text-grey-primary">
            Home
          </Link>
        </li>
      </ul>
    </nav>
  </header>
);

export default HeaderComponent;
