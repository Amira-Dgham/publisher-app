import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Publication } from '../models/publication.model';

@Injectable({ providedIn: 'root' })
export class PublicationService {
  private readonly baseUrl = '/publications';

  constructor(private api: ApiService) {}

  getAll(params?: any) {
    return this.api.get<any>(this.baseUrl, { params });
  }

  getById(id: number) {
    return this.api.get<Publication>(`${this.baseUrl}/${id}`);
  }

  create(data: any) {
    return this.api.post<Publication>(this.baseUrl, data);
  }

  update(id: number, data: Partial<Publication>) {
    return this.api.put<Publication>(`${this.baseUrl}/${id}`, data);
  }

  delete(id: number) {
    return this.api.delete(`${this.baseUrl}/${id}`);
  }
} 