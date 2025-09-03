import React from 'react';
import { cn } from '../../../design-system/utils';

export interface AppLayoutProps {
  header?: React.ReactNode;
  sidebar?: React.ReactNode;
  footer?: React.ReactNode;
  children?: React.ReactNode;
  className?: string;
  maxWidth?: 'mobile' | 'tablet' | 'desktop' | 'full';
  padding?: boolean;
}

export const AppLayout: React.FC<AppLayoutProps> = ({
  header,
  sidebar,
  footer,
  children,
  className,
  maxWidth = 'mobile',
  padding = true,
}) => {
  return (
    <div className={cn('min-h-screen bg-background font-sans antialiased', className)}>
      {header && (
        <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
          <div className="family-container">
            {header}
          </div>
        </header>
      )}
      
      <div className="flex min-h-[calc(100vh-4rem)]">
        {sidebar && (
          <aside className="w-64 border-r bg-muted/40">
            {sidebar}
          </aside>
        )}
        
        <main className="flex-1">
          <div className={cn(
            'family-container',
            padding && 'py-6'
          )}>
            {children}
          </div>
        </main>
      </div>
      
      {footer && (
        <footer className="border-t bg-background">
          <div className="family-container py-4">
            {footer}
          </div>
        </footer>
      )}
    </div>
  );
};