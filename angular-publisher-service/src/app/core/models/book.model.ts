export interface Book {
  data: any;
  id: number;
  title: string;
  publicationDate: string;
  isbn: string;
  author: AuthorSummary;
}

export interface BookCreateRequest {
  title: string;
  publicationDate: string;
  isbn: string;
  authorId: number;
}

export interface AuthorSummary {
  id: number;
  name: string;
  nationality?: string;
  birthDate?: string;
} 