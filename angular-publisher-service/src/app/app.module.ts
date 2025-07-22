import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { BooksModule } from './books/books.module';
import { ButtonModule } from 'primeng/button';

@NgModule({
  imports: [
    BrowserModule,
    AppComponent,
    BrowserAnimationsModule,
    FormsModule,
    BooksModule,
    ButtonModule
  ],
  providers: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AppModule { }