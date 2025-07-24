import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Publication } from '../models/publication.model';
import { PublicationPageResponse } from '../types/publication-page.model';

@Injectable({ providedIn: 'root' })
export class PublicationService {
  private readonly baseUrl = '/publications';

  constructor(private api: ApiService) {}

  getAll(page: number = 0, size: number = 10, sort: 'ASC' | 'DESC' = 'DESC') {
    return this.api.get<PublicationPageResponse>(this.baseUrl, { params: { page, size, sort } });
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

  searchByTitle(title: string, page: number = 0, size: number = 10, sort: 'ASC' | 'DESC' = 'DESC') {
    return this.api.get<PublicationPageResponse>(
      `${this.baseUrl}/search/title`,
      { params: { title, page, size, sort } }
    );
  }
} 