export interface Author {
  id: number;
  name: string;
  birthDate?: string;
  nationality?: string;
  books?: BookSummary[];
  magazines?: MagazineSummary[];
}

export interface AuthorCreateRequest {
  name: string;
  birthDate?: string;
  nationality?: string;
}

export interface AuthorSummary {
  id: number;
  name: string;
  nationality?: string;
  birthDate?: string;
}

export interface BookSummary {
  id: number;
  title: string;
  publicationDate: string;
  type: string;
  isbn: string;
  authorName: string;
}

export interface MagazineSummary {
  id: number;
  title: string;
  publicationDate: string;
  type: string;
  issueNumber: number;
} 