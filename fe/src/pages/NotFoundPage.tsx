import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Home, ArrowLeft } from 'lucide-react';

const NotFoundPage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div className="app-shell flex flex-col items-center justify-center p-4 text-center">
      <div className="text-4xl font-bold text-primary/30 mb-2">404</div>
      <h2 className="text-sm font-medium text-foreground">페이지를 찾을 수 없습니다</h2>
      <p className="text-[10px] text-muted-foreground mt-0.5 mb-4">존재하지 않거나 이동되었습니다</p>
      <div className="flex gap-2">
        <Button size="sm" onClick={() => navigate('/')}>
          <Home className="w-3.5 h-3.5" strokeWidth={1.5} /> 홈으로
        </Button>
        <Button variant="outline" size="sm" onClick={() => navigate(-1)}>
          <ArrowLeft className="w-3.5 h-3.5" strokeWidth={1.5} /> 뒤로
        </Button>
      </div>
    </div>
  );
};

export default NotFoundPage;
