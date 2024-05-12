import { Link } from 'react-router-dom';
import logo from '../logo.png';

const HeaderComponent = () => (
  <header className="flex p-2 h-20">
    <img alt="DataArt Logo" src={logo} className="w-[64px] h-[64px]" />
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
