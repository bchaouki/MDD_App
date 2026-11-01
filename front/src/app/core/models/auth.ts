export interface LoginRequest {
  /** E-mail ou nom d'utilisateur. */
  identifier: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
}
