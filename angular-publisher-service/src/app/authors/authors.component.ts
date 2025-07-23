import { Component, OnInit } from '@angular/core';
import { Author, AuthorCreateRequest } from '../models/author.model';
import { AuthorService } from '../services/author.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SharedModule } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { TableModule } from 'primeng/table';
import { ConfirmDialogComponent } from '../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-authors',
  templateUrl: './authors.component.html',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TableModule,
    DialogModule,
    ButtonModule,
    InputTextModule,
    SharedModule,
    ConfirmDialogComponent

  ],
  styleUrls: ['./authors.component.css']
})
export class AuthorsComponent implements OnInit {
  authors: Author[] = [];
  selectedAuthor: Author | null = null;
  displayDialog = false;
  isEdit = false;
  confirmDelete = false;
  authorToDelete: Author | null = null;
  loading = false;

  form: AuthorCreateRequest = { name: '', birthDate: '', nationality: '' };

  constructor(private authorService: AuthorService) {}

  ngOnInit() {
    this.loadAuthors();
  }

  loadAuthors() {
    this.loading = true;
    this.authorService.getAll().then(res => {
      this.authors = res.data || [];
      this.loading = false;
    }).catch(() => this.loading = false);
  }

  openNew() {
    this.isEdit = false;
    this.form = { name: '', birthDate: '', nationality: '' };
    this.displayDialog = true;
  }

  openEdit(author: Author) {
    this.isEdit = true;
    this.selectedAuthor = author;
    this.form = { name: author.name, birthDate: author.birthDate, nationality: author.nationality };
    this.displayDialog = true;
  }

  save() {
    if (this.isEdit && this.selectedAuthor) {
      this.authorService.update(this.selectedAuthor.id, this.form).then(() => {
        this.loadAuthors();
        this.displayDialog = false;
      });
    } else {
      this.authorService.create(this.form).then(() => {
        this.loadAuthors();
        this.displayDialog = false;
      });
    }
  }

  confirmDeleteAuthor(author: Author) {
    this.authorToDelete = author;
    this.confirmDelete = true;
  }

  deleteAuthor() {
    if (this.authorToDelete) {
      this.authorService.delete(this.authorToDelete.id).then(() => {
        this.loadAuthors();
        this.confirmDelete = false;
        this.authorToDelete = null;
      });
    }
  }
} 