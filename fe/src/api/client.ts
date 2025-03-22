import axios from "axios";
import type { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse, AxiosError } from "axios";
import type { AxiosRequestConfig } from "axios";
import { ApiError } from "../types/error";

interface ErrorResponse {
  error: {
    code: string;
    message: string;
  };
}

export class ApiClient {
  private static instance: ApiClient;
  private client: AxiosInstance;
  private errorHandler: ((error: ApiError) => void) | undefined;

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

  public static getInstance(): ApiClient {
    if (!ApiClient.instance) {
      ApiClient.instance = new ApiClient();
    }
    return ApiClient.instance;
  }

  public setErrorHandler(handler: (error: ApiError) => void): void {
    this.errorHandler = handler;
  }

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
          const { status, data } = error.response;
          const apiError: ApiError = {
            code: data.error.code,
            message: data.error.message,
            status
          };

          if (this.errorHandler) {
            this.errorHandler(apiError);
          }
        } else if (error.request) {
          // 요청은 보냈으나 응답을 받지 못한 경우
          console.error("서버로부터 응답이 없습니다.");
        } else {
          // 요청 설정 중 오류 발생
          console.error("요청 config에서 오류 발생:", error.message);
        }

        return Promise.reject(error);
      }
    );
  }

  public async get<T>(url: string, config?: InternalAxiosRequestConfig): Promise<T> {
    const response = await this.client.get<T>(url, config);
    return response.data;
  }

  public async post<T>(
    url: string,
    data?: unknown,
    config?: InternalAxiosRequestConfig
  ): Promise<T> {
    const response = await this.client.post<T>(url, data, config);
    return response.data;
  }

  public async put<T>(
    url: string,
    data?: unknown,
    config?: InternalAxiosRequestConfig
  ): Promise<T> {
    const response = await this.client.put<T>(url, data, config);
    return response.data;
  }

  public async delete<T>(url: string, config?: InternalAxiosRequestConfig): Promise<T> {
    const response = await this.client.delete<T>(url, config);
    return response.data;
  }
}
