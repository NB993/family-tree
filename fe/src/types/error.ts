/**
 * ApiError 인터페이스
 * API 호출 중 발생하는 오류를 정의합니다.
 *
 * @property {string} code - 오류 코드 (예: C001, A002, S003 등).
 * @property {string} message - 상세한 오류 메시지.
 * @property {number} status - HTTP 상태 코드 (예: 400, 404, 500 등).
 * @property {string} traceId -
 * @property {FieldError[]} [validations] - 유효성 검증 오류가 있을 경우, 각 필드에 대한 상세 오류 정보를 포함하는 배열.
 */
export interface ApiError {
  code: string;
  message: string;
  status: number;
  traceId?: string;
  validations?: FieldError[];
}

/**
 * FieldError 인터페이스
 * 유효성 검증 실패 시, 특정 필드에 관련된 에러 정보를 정의합니다.
 * 백엔드 FieldError 구조와 일치합니다.
 *
 * @property {string} field - 유효성 검증이 실패한 필드 이름.
 * @property {unknown} value - 서버가 거부한 값.
 * @property {string} message - 해당 필드에 대한 상세한 오류 메시지.
 */
export interface FieldError {
  field: string;
  value: any;
  message: string;
}


/**
 * ErrorHandlers 인터페이스
 * 에러 코드와 그에 해당하는 에러 핸들러를 매핑합니다.
 *
 * @property {Record<string, (error: ApiError) => void>} [code] -
 * 에러 코드를 키로 하고, 특정 에러를 처리하는 핸들러를 값으로 가지는 객체입니다.
 */
export interface ErrorHandlers {
  [code: string]: (error: ApiError) => void; // 에러 코드에 매핑된 핸들러
}

/**
 * API 요청에서 실패 시 반환되는 에러를 설명하는 인터페이스입니다.
 * 서버의 ErrorResponse 클래스 구조와 대응됩니다.
 * 
 * 속성 설명:
 * - `code`: 에러 유형 또는 범주를 고유하게 식별하는 문자열입니다. ex) 'C001'
 * - `message`: 에러를 설명하는 사람 읽을 수 있는 메시지입니다.
 * - `traceId`: 요청을 추적하기 위한 고유 식별자입니다.
 * - `validations` (선택): 검증 실패에 대한 추가적인 문맥을 제공하는 필드별 에러 배열입니다.
 */
export interface ErrorResponse {
  error: {
    code: string;
    message: string;
    traceId: string;
    validations?: FieldError[];
  };
}
