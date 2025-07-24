import { CUSTOM_ELEMENTS_SCHEMA, ErrorHandler, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { SharedModule } from './shared/shared.module';
import { PublicationsComponent } from './features/publications/publications.component';
import { MagazinesComponent } from './features/magazines/magazines.component';
import { AuthorsComponent } from './features/authors/authors.component';
import { BooksComponent } from './features/books/books.component';
import { MessageService } from 'primeng/api';
import { GlobalErrorHandler } from './core/handlers/global-error.handler';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { ErrorInterceptor } from './core/interceptors/error.interceptor';

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
  providers: [
    MessageService,   // PrimeNG MessageService
    {
      provide: ErrorHandler,
      useClass: GlobalErrorHandler
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ErrorInterceptor,
      multi: true
    }
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class AppModule {}