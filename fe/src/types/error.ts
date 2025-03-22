export interface ApiError {
  code: string;      // C001, A002, S003 등
  message: string;   // 에러 메시지
  status: number;    // HTTP 상태 코드
}

export interface ErrorHandler {
  handle: (error: ApiError) => void;
  priority: number;
}

export interface ErrorHandlers {
  [code: string]: ErrorHandler;
} 