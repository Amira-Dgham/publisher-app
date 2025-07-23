import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { SharedModule } from './shared/shared.module';
import { PublicationsComponent } from './publications/publications.component';
import { MagazinesComponent } from './magazines/magazines.component';
import { AuthorsComponent } from './authors/authors.component';
import { BooksComponent } from './books/books.component';

@NgModule({
  imports: [
    AppComponent,
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    PublicationsComponent,
    MagazinesComponent,
    AuthorsComponent,
    BooksComponent,
    SharedModule
  ],
  providers: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class AppModule {}