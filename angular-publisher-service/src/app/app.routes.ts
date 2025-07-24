import { Routes } from '@angular/router';
import { BooksComponent } from './features/books/books.component';
import { MagazinesComponent } from './features/magazines/magazines.component';
import { AuthorsComponent } from './features/authors/authors.component';
import { PublicationsComponent } from './features/publications/publications.component';

export const routes: Routes = [
  { path: 'books', component: BooksComponent },
  { path: 'magazines', component: MagazinesComponent },
  { path: 'authors', component: AuthorsComponent },
  { path: 'publications', component: PublicationsComponent },
  { path: '', redirectTo: 'books', pathMatch: 'full' },
];
