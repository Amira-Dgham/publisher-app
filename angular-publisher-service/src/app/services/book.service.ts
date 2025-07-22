import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Book, BookCreateRequest } from '../models/book.model';

@Injectable({ providedIn: 'root' })
export class BookService {
  private readonly baseUrl = '/books';

  constructor(private api: ApiService) {}

  getAll(params?: any) {
    return this.api.get<any>(this.baseUrl, { params });
  }

  getById(id: number) {
    return this.api.get<Book>(`${this.baseUrl}/${id}`);
  }

  create(data: BookCreateRequest) {
    return this.api.post<Book>(this.baseUrl, data);
  }

  update(id: number, data: Partial<BookCreateRequest>) {
    return this.api.put<Book>(`${this.baseUrl}/${id}`, data);
  }

  delete(id: number) {
    return this.api.delete(`${this.baseUrl}/${id}`);
  }
} 