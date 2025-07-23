import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Magazine, MagazineCreateRequest } from '../models/magazine.model';

@Injectable({ providedIn: 'root' })
export class MagazineService {
  private readonly baseUrl = '/magazines';

  constructor(private api: ApiService) {}

  getAll(params?: any) {
    return this.api.get<any>(this.baseUrl, { params });
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