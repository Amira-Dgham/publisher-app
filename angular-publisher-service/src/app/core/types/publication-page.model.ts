import { Publication } from '../models/publication.model';

export interface PublicationPageResponse {
    success: boolean;
    message: string;
    data: PublicationPageData;
  }

export interface PublicationPageData {
  content: Publication[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
} 