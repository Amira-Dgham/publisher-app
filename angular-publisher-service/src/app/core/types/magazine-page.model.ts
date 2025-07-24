import { Magazine } from '../models/magazine.model';

export interface MagazinePageResponse {
    success: boolean;
    message: string;
    data: MagazinePageData;
  }
  
export interface MagazinePageData {
  content: Magazine[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
} 