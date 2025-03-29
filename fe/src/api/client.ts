import axios from "axios";
import type {AxiosInstance, InternalAxiosRequestConfig, AxiosResponse, AxiosError} from "axios";
import {ApiError, FieldError} from "../types/error";

/**
 * API 요청에서 실패 시 반환되는 에러를 설명하는 인터페이스입니다.
 * 서버의 ErrorResponse 클래스 구조와 대응됩니다.
 *
 * 이 인터페이스는 API나 서비스로부터 반환된 에러 세부 정보를 캡슐화하여 제공합니다.
 * `error` 속성은 에러의 성격 및 원인에 대한 중심 정보를 포함합니다.
 *
 * 속성 설명:
 * - `code`: 에러 유형 또는 범주를 고유하게 식별하는 문자열입니다. ex) 'C001'
 * - `message`: 에러를 설명하는 사람 읽을 수 있는 메시지입니다.
 * - `validations` (선택): 검증 실패에 대한 추가적인 문맥을 제공하는 필드별 에러 배열입니다.
 */
interface ErrorResponse {
  error: {
    code: string;
    message: string;
    validations?: FieldError[];
  };
}

/**
 * API 요청을 관리하는 클라이언트 클래스.
 * 싱글턴 패턴을 사용해 전역적으로 하나의 인스턴스를 공유하며,
 * HTTP 요청 및 응답 처리를 담당합니다.
 */
export class ApiClient {
  private static instance: ApiClient;
  private client: AxiosInstance;
  private errorHandler: ((error: ApiError) => void) | undefined;

  /**
   * 클래스의 생성자.
   * Axios 인스턴스를 생성하고, 요청/응답 인터셉터를 설정합니다.
   * @private
   */
  private constructor() {
    this.client = axios.create({
      baseURL: process.env.REACT_APP_API_URL,
      timeout: 10000,
      headers: {
        "Content-Type": "application/json",
      },
    });

    this.setupInterceptors();
  }

  /**
   * ApiClient의 싱글턴 인스턴스를 반환합니다.
   * 초기화되지 않았다면 인스턴스를 생성합니다.
   * @returns {ApiClient} ApiClient의 유일한 인스턴스.
   */
  public static getInstance(): ApiClient {
    if (!ApiClient.instance) {
      ApiClient.instance = new ApiClient();
    }
    return ApiClient.instance;
  }

  /**
   * 에러 핸들러를 설정합니다.
   * 지정된 핸들러는 API 요청/응답 중 발생한 에러를 처리하는 데 사용됩니다.
   * @param handler {(error: ApiError) => void} 에러 처리 함수.
   */
  public setErrorHandler(handler: (error: ApiError) => void): void {
    this.errorHandler = handler;
  }

  /**
   * Axios 요청 및 응답 인터셉터를 설정합니다.
   * - 요청 인터셉터: 요청 전 설정을 조정하거나 에러를 처리합니다.
   * - 응답 인터셉터: 응답 또는 응답 에러를 처리합니다.
   * @private
   */
  private setupInterceptors(): void {
    // 요청 인터셉터: HTTP 요청이 실제로 전송되기 전에 발생하는 에러를 처리
    // 예: 잘못된 URL 형식, 요청 헤더 설정 오류, 요청 데이터 변환 오류, 네트워크 연결 실패
    this.client.interceptors.request.use(
        (config: InternalAxiosRequestConfig) => config,
        (error: AxiosError) => Promise.reject(error)
    );

    // 응답 인터셉터에서 에러 처리를 위한 설정
    this.client.interceptors.response.use(
        (response: AxiosResponse) => response,
        (error: AxiosError<ErrorResponse>) => {
          if (error.response) {
            const {status, data} = error.response;
            const apiError: ApiError = {
              code: data.error.code,
              message: data.error.message,
              validations: data.error.validations,
              status
            };

            if (this.errorHandler) {
              this.errorHandler(apiError);
            }
          }

          if (error.request) {
            console.error("서버로부터 응답이 없습니다.");
            //todo Unexpected Exception을 처리할 모달 노출
          }

          console.error("exception: ", error.message);
          //todo Unexpected Exception을 처리할 모달 노출
        }
    );
  }

  /**
   * HTTP GET 요청을 보냅니다.
   * @template T 요청 결과로 반환받을 데이터의 타입.
   * @param {string} url 요청할 URL.
   * @param {InternalAxiosRequestConfig} [config] 추가 요청 설정.
   * @returns {Promise<T>} 요청 결과 데이터를 포함한 Promise.
   */
  public async get<T>(url: string, config?: InternalAxiosRequestConfig): Promise<T> {
    const response = await this.client.get<T>(url, config);
    return response.data;
  }

  /**
   * HTTP POST 요청을 보냅니다.
   * @template T 요청 결과로 반환받을 데이터의 타입.
   * @param {string} url 요청할 URL.
   * @param {unknown} [data] 요청에 포함할 데이터.
   * @param {InternalAxiosRequestConfig} [config] 추가 요청 설정.
   * @returns {Promise<T>} 요청 결과 데이터를 포함한 Promise.
   */
  public async post<T>(
      url: string,
      data?: unknown,
      config?: InternalAxiosRequestConfig
  ): Promise<T> {
    const response = await this.client.post<T>(url, data, config);
    return response.data;
  }

  /**
   * HTTP PUT 요청을 보냅니다.
   * @template T 요청 결과로 반환받을 데이터의 타입.
   * @param {string} url 요청할 URL.
   * @param {unknown} [data] 요청에 포함할 데이터.
   * @param {InternalAxiosRequestConfig} [config] 추가 요청 설정.
   * @returns {Promise<T>} 요청 결과 데이터를 포함한 Promise.
   */
  public async put<T>(
      url: string,
      data?: unknown,
      config?: InternalAxiosRequestConfig
  ): Promise<T> {
    const response = await this.client.put<T>(url, data, config);
    return response.data;
  }

  /**
   * HTTP DELETE 요청을 보냅니다.
   * @template T 요청 결과로 반환받을 데이터의 타입.
   * @param {string} url 요청할 URL.
   * @param {InternalAxiosRequestConfig} [config] 추가 요청 설정.
   * @returns {Promise<T>} 요청 결과 데이터를 포함한 Promise.
   */
  public async delete<T>(url: string, config?: InternalAxiosRequestConfig): Promise<T> {
    const response = await this.client.delete<T>(url, config);
    return response.data;
  }
}
