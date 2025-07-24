import { Book } from '../models/book.model';

export interface BookPageResponse {
  success: boolean;
  message: string;
  data: BookPageData;
}

export interface BookPageData {
  content: Book[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number; // current page index (0-based)
}