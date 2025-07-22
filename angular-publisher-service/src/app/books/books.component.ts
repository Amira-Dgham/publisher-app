import { Component, OnInit } from '@angular/core';
import { Book, BookCreateRequest } from '../models/book.model';
import { BookService } from '../services/book.service';
import { TableModule } from 'primeng/table';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { FormsModule } from '@angular/forms';
import { ConfirmDialogComponent } from '../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-books',
  templateUrl: './books.component.html',
  styleUrls: ['./books.component.css'],
  standalone: true,
  imports: [
    TableModule,
    DialogModule,
    ButtonModule,
    InputTextModule,
    FormsModule,
    ConfirmDialogComponent
  ]
})
export class BooksComponent implements OnInit {
  books: Book[] = [];
  selectedBook: Book | null = null;
  displayDialog = false;
  isEdit = false;
  confirmDelete = false;
  bookToDelete: Book | null = null;
  loading = false;

  form: BookCreateRequest = { 
    title: '', 
    publicationDate: '', 
    isbn: '', 
    authorId: 0 
  };

  constructor(private bookService: BookService) {}

  ngOnInit() {
    this.loadBooks();
  }

  loadBooks() {
    this.loading = true;
    this.bookService.getAll()
      .then(res => {
        this.books = res.data.data?.content || res.data || [];
        this.loading = false;
      })
      .catch(() => this.loading = false);
  }

  openNew() {
    this.isEdit = false;
    this.form = { 
      title: '', 
      publicationDate: '', 
      isbn: '', 
      authorId: 0 
    };
    this.displayDialog = true;
  }

  openEdit(book: Book) {
    this.isEdit = true;
    this.selectedBook = book;
    this.form = { 
      title: book.title, 
      publicationDate: book.publicationDate, 
      isbn: book.isbn, 
      authorId: book.author.id 
    };
    this.displayDialog = true;
  }

  save() {
    if (this.isEdit && this.selectedBook) {
      this.bookService.update(this.selectedBook.id, this.form)
        .then(() => {
          this.loadBooks();
          this.displayDialog = false;
        });
    } else {
      this.bookService.create(this.form)
        .then(() => {
          this.loadBooks();
          this.displayDialog = false;
        });
    }
  }

  confirmDeleteBook(book: Book) {
    this.bookToDelete = book;
    this.confirmDelete = true;
  }

  deleteBook() {
    if (this.bookToDelete) {
      this.bookService.delete(this.bookToDelete.id)
        .then(() => {
          this.loadBooks();
          this.confirmDelete = false;
          this.bookToDelete = null;
        });
    }
  }
}