import { Routes } from '@angular/router';
import { BooksComponent } from './books/books.component';
import { MagazinesComponent } from './magazines/magazines.component';
import { AuthorsComponent } from './authors/authors.component';
import { PublicationsComponent } from './publications/publications.component';

export const routes: Routes = [
  { path: 'books', component: BooksComponent },
  { path: 'magazines', component: MagazinesComponent },
  { path: 'authors', component: AuthorsComponent },
  { path: 'publications', component: PublicationsComponent },
  { path: '', redirectTo: 'books', pathMatch: 'full' },
];
