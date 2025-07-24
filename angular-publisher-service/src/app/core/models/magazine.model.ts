export interface Magazine {
  id: number;
  title: string;
  publicationDate: string;
  issueNumber: number;
  authors: AuthorSummary[];
}

export interface MagazineCreateRequest {
  title: string;
  publicationDate: string;
  issueNumber: number;
  authorIds: number[];
}

export interface AuthorSummary {
  id: number;
  name: string;
  nationality?: string;
  birthDate?: string;
} 