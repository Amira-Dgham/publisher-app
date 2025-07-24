import { Injectable } from '@angular/core';
import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private axiosInstance: AxiosInstance;

  constructor() {
    this.axiosInstance = axios.create({
      baseURL: environment.apiBaseUrl,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }

  public get<T>(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.axiosInstance.get<T>(url, config).catch(this.handleError);
  }

  public post<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.axiosInstance.post<T>(url, data, config).catch(this.handleError);
  }

  public put<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.axiosInstance.put<T>(url, data, config).catch(this.handleError);
  }

  public delete<T>(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.axiosInstance.delete<T>(url, config).catch(this.handleError);
  }

  private handleError(error: any): never {
    // Centralized error handling
    // Optionally add notification logic here
    throw error;
  }
} 