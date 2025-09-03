import React from 'react';
import { cn } from '../../../design-system/utils';

export interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
  variant?: 'default' | 'elevated' | 'outlined';
  padding?: 'sm' | 'md' | 'lg';
  clickable?: boolean;
  children?: React.ReactNode;
}

export const Card = React.forwardRef<HTMLDivElement, CardProps>(
  (
    {
      variant = 'default',
      padding = 'md',
      clickable = false,
      className,
      children,
      ...props
    },
    ref
  ) => {
    const baseStyles = 'rounded-xl border border-border bg-card text-card-foreground shadow-family';
    const variants = {
      default: '',
      elevated: 'shadow-family-md',
      outlined: 'border-2',
    };
    
    const cardClassName = cn(
      baseStyles,
      variants[variant],
      clickable && 'cursor-pointer hover:shadow-family-md hover:-translate-y-1 transition-all duration-300 active:scale-[0.98]',
      className
    );
    
    return (
      <div ref={ref} className={cardClassName} {...props}>
        {children}
      </div>
    );
  }
);

Card.displayName = 'Card';

export interface CardHeaderProps extends React.HTMLAttributes<HTMLDivElement> {
  children?: React.ReactNode;
}

export const CardHeader = React.forwardRef<HTMLDivElement, CardHeaderProps>(
  ({ className, children, ...props }, ref) => {
    return (
      <div ref={ref} className={cn('flex flex-col space-y-1.5 p-6', className)} {...props}>
        {children}
      </div>
    );
  }
);

CardHeader.displayName = 'CardHeader';

export interface CardContentProps extends React.HTMLAttributes<HTMLDivElement> {
  children?: React.ReactNode;
}

export const CardContent = React.forwardRef<HTMLDivElement, CardContentProps>(
  ({ className, children, ...props }, ref) => {
    return (
      <div ref={ref} className={cn('p-6 pt-0', className)} {...props}>
        {children}
      </div>
    );
  }
);

CardContent.displayName = 'CardContent';

export interface CardFooterProps extends React.HTMLAttributes<HTMLDivElement> {
  children?: React.ReactNode;
}

export const CardFooter = React.forwardRef<HTMLDivElement, CardFooterProps>(
  ({ className, children, ...props }, ref) => {
    return (
      <div ref={ref} className={cn('flex items-center p-6 pt-0', className)} {...props}>
        {children}
      </div>
    );
  }
);

CardFooter.displayName = 'CardFooter';