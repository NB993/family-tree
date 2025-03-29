/**
 * ApiError 인터페이스
 * API 호출 중 발생하는 오류를 정의합니다.
 *
 * @property {string} code - 오류 코드 (예: C001, A002, S003 등).
 * @property {string} message - 상세한 오류 메시지.
 * @property {number} status - HTTP 상태 코드 (예: 400, 404, 500 등).
 * @property {FieldError[]} [validations] - 유효성 검증 오류가 있을 경우, 각 필드에 대한 상세 오류 정보를 포함하는 배열.
 */
export interface ApiError {
  code: string;
  message: string;
  status: number;
  validations?: FieldError[];
}


/**
 * FieldError 인터페이스
 * 유효성 검증 실패 시, 특정 필드에 관련된 에러 정보를 정의합니다.
 *
 * @property {string} field - 유효성 검증이 실패한 필드 이름.
 * @property {unknown} value - 클라이언트가 보낸 잘못된 값.
 * @property {string} message - 해당 필드에 대한 상세한 오류 메시지.
 */
// FieldError 정의
export interface FieldError {
  field: string;
  value: unknown;
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
