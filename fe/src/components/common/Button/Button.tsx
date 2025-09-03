import React from 'react';
import { cn } from '../../../design-system/utils';

export type ButtonVariant = 'primary' | 'secondary' | 'outline' | 'ghost' | 'destructive';
export type ButtonSize = 'sm' | 'md' | 'lg' | 'xl';

export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant;
  size?: ButtonSize;
  fullWidth?: boolean;
  loading?: boolean;
  icon?: React.ReactNode;
  iconPosition?: 'left' | 'right';
  children?: React.ReactNode;
}

const buttonVariants = {
  primary: 'family-button-primary',
  secondary: 'family-button-secondary',
  outline: 'border border-input bg-background shadow-sm hover:bg-accent hover:text-accent-foreground',
  ghost: 'hover:bg-accent hover:text-accent-foreground',
  destructive: 'bg-destructive text-destructive-foreground shadow-sm hover:bg-destructive/90',
};

const buttonSizes = {
  sm: 'h-8 rounded-md px-3 text-xs',
  md: 'h-10 px-4 py-2',
  lg: 'h-12 rounded-md px-8',
  xl: 'h-14 rounded-md px-8 text-lg',
};

export const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  (
    {
      variant = 'primary',
      size = 'md',
      fullWidth = false,
      loading = false,
      icon,
      iconPosition = 'left',
      className,
      disabled,
      children,
      ...props
    },
    ref
  ) => {
    const baseStyles = 'inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:pointer-events-none disabled:opacity-50';
    
    const buttonClassName = cn(
      baseStyles,
      buttonVariants[variant],
      buttonSizes[size],
      fullWidth && 'w-full',
      className
    );
    
    return (
      <button
        ref={ref}
        className={buttonClassName}
        disabled={disabled || loading}
        {...props}
      >
        {loading ? (
          <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-current"></div>
        ) : (
          <>
            {icon && iconPosition === 'left' && icon}
            {children}
            {icon && iconPosition === 'right' && icon}
          </>
        )}
      </button>
    );
  }
);

Button.displayName = 'Button';