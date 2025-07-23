import { Author } from '../models/author.model';

export interface AuthorPageResponse {
  success: boolean;
  message: string;
  data: AuthorPageData;
}

export interface AuthorPageData {
  content: Author[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}