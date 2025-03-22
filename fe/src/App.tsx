import React, { useEffect } from 'react';
import { useApiError } from './hooks/useApiError';
import { ApiClient } from './api/client';
import { ErrorHandlers } from './types/error';

const customHandlers: ErrorHandlers = {
  S001: {
    handle: (error) => {
      console.error('학생 정보 없음:', error.message);
    },
    priority: 1
  }
};

function App() {
  const { handleError } = useApiError(customHandlers);

  useEffect(() => {
    const apiClient = ApiClient.getInstance();
    apiClient.setErrorHandler(handleError);
  }, [handleError]);

  return (
    <div className="App">
      {/* 기존 컴포넌트 내용 */}
    </div>
  );
}

export default App; 