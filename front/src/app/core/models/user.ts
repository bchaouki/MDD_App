export interface User {
  id: number;
  username: string;
  email: string;
  createdAt: string;
}

export interface UpdateUserRequest {
  username: string;
  email: string;
  /** Absent : le mot de passe actuel est conservé. */
  password?: string;
}

export interface UpdateUserResponse {
  user: User;
  token: string;
}
