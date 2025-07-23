import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Magazine, MagazineCreateRequest } from '../models/magazine.model';
import { MagazinePageResponse } from '../types/magazine-page.model';

@Injectable({ providedIn: 'root' })
export class MagazineService {
  private readonly baseUrl = '/magazines';

  constructor(private api: ApiService) {}

  getAll(page: number = 0, size: number = 10, sort: 'ASC' | 'DESC' = 'DESC') {
    return this.api.get<MagazinePageResponse>(this.baseUrl, { params: { page, size, sort } });
  }

  getById(id: number) {
    return this.api.get<Magazine>(`${this.baseUrl}/${id}`);
  }

  create(data: MagazineCreateRequest) {
    return this.api.post<Magazine>(this.baseUrl, data);
  }

  update(id: number, data: Partial<MagazineCreateRequest>) {
    return this.api.put<Magazine>(`${this.baseUrl}/${id}`, data);
  }

  delete(id: number) {
    return this.api.delete(`${this.baseUrl}/${id}`);
  }
} 