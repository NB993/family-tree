import React from 'react';
import { Link } from 'react-router-dom';
import { Card, CardContent } from '../components/common/Card';
import { Button } from '../components/common/Button';

const NotFoundPage: React.FC = () => {
  return (
    <div className="container">
      <div className="flex items-center justify-center min-h-screen">
        <Card className="text-center max-w-md">
          <CardContent>
            <div className="py-12">
              <div className="text-8xl mb-6">😕</div>
              <h1 className="text-3xl font-bold text-gray-900 mb-4">
                페이지를 찾을 수 없습니다
              </h1>
              <p className="text-gray-600 mb-8">
                요청하신 페이지가 존재하지 않거나 이동되었을 수 있습니다.
              </p>
              <div className="space-y-3">
                <Link to="/home">
                  <Button variant="primary" size="lg" fullWidth>
                    홈으로 돌아가기
                  </Button>
                </Link>
                <Button
                  variant="ghost"
                  size="lg"
                  fullWidth
                  onClick={() => window.history.back()}
                >
                  이전 페이지로
                </Button>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default NotFoundPage;