export interface AxiosErrorLike {
  response?: {
    data?: string;
  };
}

export interface ApiErrorResponse {
  errors?: ValidationError[];
  message?: string;
  [key: string]: unknown;
}

interface ValidationError {
  msg: string;
}