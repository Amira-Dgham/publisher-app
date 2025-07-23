import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Author, AuthorCreateRequest } from '../models/author.model';
import { AuthorPageResponse } from '../types/author-page.model';

@Injectable({ providedIn: 'root' })
export class AuthorService {
  private readonly baseUrl = '/authors';

  constructor(private api: ApiService) {}

  getAll(page: number = 0, size: number = 10, sort: 'ASC' | 'DESC' = 'DESC') {
    return this.api.get<AuthorPageResponse>(this.baseUrl, { params: { page, size, sort } });
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