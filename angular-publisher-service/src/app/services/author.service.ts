import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Author, AuthorCreateRequest } from '../models/author.model';

@Injectable({ providedIn: 'root' })
export class AuthorService {
  private readonly baseUrl = '/authors';

  constructor(private api: ApiService) {}

  getAll(params?: any) {
    return this.api.get<any>(this.baseUrl, { params });
  }

  getById(id: number) {
    return this.api.get<Author>(`${this.baseUrl}/${id}`);
  }

  create(data: AuthorCreateRequest) {
    return this.api.post<Author>(this.baseUrl, data);
  }

  update(id: number, data: Partial<AuthorCreateRequest>) {
    return this.api.put<Author>(`${this.baseUrl}/${id}`, data);
  }

  delete(id: number) {
    return this.api.delete(`${this.baseUrl}/${id}`);
  }
} 