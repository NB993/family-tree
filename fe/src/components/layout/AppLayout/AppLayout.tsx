import React from 'react';
import { cn } from '@/lib/utils';

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
  children,
}) => {
  return <>{children}</>;
};