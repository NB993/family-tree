import axios from "axios";
import { ApiError } from "../types/api";

export class CustomApiError extends Error {
  constructor(
    public message: string,
    public code: string,
    public status: number
  ) {
    super(message);
    this.name = "CustomApiError";
  }
}

export function handleApiError(error: unknown): CustomApiError {
  if (error instanceof CustomApiError) {
    return error;
  }

  if (axios.isAxiosError(error)) {
    return new CustomApiError(
      error.response?.data?.message || "알 수 없는 오류가 발생했습니다.",
      error.response?.data?.code || "UNKNOWN_ERROR",
      error.response?.status || 500
    );
  }

  return new CustomApiError(
    "알 수 없는 오류가 발생했습니다.",
    "UNKNOWN_ERROR",
    500
  );
}
