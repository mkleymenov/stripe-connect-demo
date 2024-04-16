import { ReactNode } from 'react';
import cn from 'classnames';

export type Props = {
  title: string;
  subtitle: string;
  style: 'success' | 'info' | 'error';
  className?: string;
  children?: ReactNode;
};

const MerchantStatusCardComponent = ({
  title,
  subtitle,
  style,
  className,
  children,
}: Props) => (
  <div
    className={cn(
      'flex flex-row content-between items-center rounded-md px-4 py-2',
      className,
      {
        'bg-green-light': style === 'success',
        'bg-blue-50': style === 'info',
        'bg-red-50': style === 'error',
      },
    )}
  >
    <div className="flex-auto">
      <h2 className="font-medium text-grey-primary">{title}</h2>
      <p className="my-1 text-grey-primary">{subtitle}</p>
    </div>
    {children && <div>{children}</div>}
  </div>
);

export default MerchantStatusCardComponent;
