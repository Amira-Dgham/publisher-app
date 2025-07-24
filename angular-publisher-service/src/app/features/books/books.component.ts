import { Component, OnInit } from '@angular/core';
import { Book, BookCreateRequest } from '../../core/models/book.model';
import { BookService } from '../../core/services/book.service';
import { TableModule } from 'primeng/table';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { FormsModule } from '@angular/forms';
import { ConfirmDialogComponent } from '../../shared/confirm-dialog/confirm-dialog.component';
import { PaginatorModule } from 'primeng/paginator';
import { BookPageData } from '../../core/types/book-page.model';
import { MessageService } from 'primeng/api';


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
    ConfirmDialogComponent,
    PaginatorModule
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

  page = 0;
  pageSize = 10;
  totalRecords = 0;
  sort: 'ASC' | 'DESC' = 'DESC';

  constructor(private bookService: BookService,private messageService: MessageService) {}

  ngOnInit() {
    this.loadBooks();
  }

  loadBooks() {
    this.loading = true;
    this.bookService.getAll(this.page, this.pageSize, this.sort).then(res => {
      const pageData: BookPageData = res.data.data;
      this.books = pageData?.content || [];
      this.totalRecords = pageData?.totalElements || 0;
      this.loading = false;
    }).catch(() => this.loading = false);
  }

  onPageChange(event: any) {
    this.page = event.page;
    this.pageSize = event.rows;
    this.loadBooks();
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
          this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Book created successfully' });

        }).catch((error:Error)=>{
          console.log("amiraaaaa"+error);

          this.messageService.add({severity:'error', summary:'Error', detail:'Error saving book'});
        })
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

