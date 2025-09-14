import axios from "axios";
import type {AxiosInstance, InternalAxiosRequestConfig, AxiosResponse, AxiosError} from "axios";
import {ApiError, FieldError, ErrorResponse} from "../types/error";


/**
 * API 요청을 관리하는 클라이언트 클래스.
 * 싱글턴 패턴을 사용해 전역적으로 하나의 인스턴스를 공유하며,
 * HTTP 요청 및 응답 처리를 담당합니다.
 */
export class ApiClient {
  private static instance: ApiClient;
  private client: AxiosInstance;
  private errorHandler: ((error: ApiError) => void) | undefined;

  private isRefreshing = false;
  private failedQueue: Array<{ resolve: (token: string | null) => void, reject: (err: unknown) => void }> = [];


  /**
   * 클래스의 생성자.
   * Axios 인스턴스를 생성하고, 요청/응답 인터셉터를 설정합니다.
   * @private
   */
  private constructor() {
    this.client = axios.create({
      baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080',
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
    // 요청 인터셉터
    this.client.interceptors.request.use(
        (config: InternalAxiosRequestConfig) => {
          config.withCredentials = true;
          return config;
        },
        (error: AxiosError) => Promise.reject(error)
    );

    // 응답 인터셉터
    this.client.interceptors.response.use(
        (response: AxiosResponse) => response,
        async (error: AxiosError<ErrorResponse>) => {
          const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

          if (error.response?.status === 401) {
            const { AuthService } = await import('../api/services/authService');
            const authService = AuthService.getInstance();

            await this.handleError(error);

            await authService.refreshAccessToken();

            return this.client(originalRequest);
          }

          if (error.response) {
            return this.handleError(error);
          }

          if (error.request) {
            console.error("서버로부터 응답이 없습니다.");
          }
          console.error("exception: ", error.message);
          return Promise.reject(error);
        }
    );
  }

  private handleError(error: AxiosError<ErrorResponse>) {
    if (error.response) {
      const { status, data } = error.response;
      const apiError: ApiError = {
        code: data.code,
        message: data.message,
        traceId: data.traceId,
        validations: data.validations,
        status,
      };

      if (this.errorHandler) {
        this.errorHandler(apiError);
      }

      return Promise.reject(apiError);
    }
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
   * HTTP PATCH 요청을 보냅니다.
   * @template T 요청 결과로 반환받을 데이터의 타입.
   * @param {string} url 요청할 URL.
   * @param {unknown} [data] 요청에 포함할 데이터.
   * @param {InternalAxiosRequestConfig} [config] 추가 요청 설정.
   * @returns {Promise<T>} 요청 결과 데이터를 포함한 Promise.
   */
  public async patch<T>(
      url: string,
      data?: unknown,
      config?: InternalAxiosRequestConfig
  ): Promise<T> {
    const response = await this.client.patch<T>(url, data, config);
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