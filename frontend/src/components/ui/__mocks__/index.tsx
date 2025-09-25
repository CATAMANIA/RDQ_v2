// Mock des composants UI pour les tests
import React from 'react';

export const Button = ({ children, onClick, disabled, className, ...props }: any) => (
  <button onClick={onClick} disabled={disabled} className={className} {...props}>
    {children}
  </button>
);

export const Card = ({ children, className, ...props }: any) => (
  <div className={className} {...props}>
    {children}
  </div>
);

export const CardContent = ({ children, className, ...props }: any) => (
  <div className={className} {...props}>
    {children}
  </div>
);

export const Alert = ({ children, className, ...props }: any) => (
  <div className={className} {...props}>
    {children}
  </div>
);

export const AlertDescription = ({ children, className, ...props }: any) => (
  <div className={className} {...props}>
    {children}
  </div>
);