import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UpdateUserRequest, UpdateUserResponse, User } from '../../core/models/user';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly url = `${environment.apiUrl}/users/me`;

  private readonly http = inject(HttpClient);

  getMe(): Observable<User> {
    return this.http.get<User>(this.url);
  }

  update(request: UpdateUserRequest): Observable<UpdateUserResponse> {
    return this.http.put<UpdateUserResponse>(this.url, request);
  }
}
