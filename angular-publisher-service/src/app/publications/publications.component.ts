import { Component, OnInit } from '@angular/core';
import { Publication } from '../models/publication.model';
import { PublicationService } from '../services/publication.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SharedModule } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { TableModule } from 'primeng/table';
import { ConfirmDialogComponent } from '../shared/confirm-dialog/confirm-dialog.component';
import { ActivatedRoute } from '@angular/router';
import { PaginatorModule } from "primeng/paginator";

@Component({
  selector: 'app-publications',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TableModule,
    DialogModule,
    ButtonModule,
    InputTextModule,
    SharedModule,
    ConfirmDialogComponent,
    PaginatorModule
],
  templateUrl: './publications.component.html',
  styleUrls: ['./publications.component.css']
})
export class PublicationsComponent implements OnInit {
  publications: Publication[] = [];
  selectedPublication: Publication | null = null;
  displayDialog = false;
  isEdit = false;
  confirmDelete = false;
  publicationToDelete: Publication | null = null;
  loading = false;

  form: Partial<Publication> = { title: '', publicationDate: '' };

  page = 0;
  pageSize = 10;
  totalRecords = 0;
  sort: 'ASC' | 'DESC' = 'DESC';

  constructor(private publicationService: PublicationService, private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const title = params['title'];
      if (title) {
        this.searchPublicationsByTitle(title);
      } else {
        this.loadPublications();
      }
    });
  }

  loadPublications() {
    this.loading = true;
    this.publicationService.getAll(this.page, this.pageSize, this.sort).then(res => {
      this.publications = res.data?.data.content || res.data || [];
      this.totalRecords = res.data?.data.totalElements || 0;
      this.loading = false;
    }).catch(() => this.loading = false);
  }

  searchPublicationsByTitle(title: string) {
    this.loading = true;
    this.publicationService.searchByTitle(title, this.page, this.pageSize, this.sort).then(res => {
      this.publications = res.data?.data.content || res.data || [];
      this.totalRecords = res.data?.data.totalElements || 0;
      this.loading = false;
    }).catch(() => this.loading = false);
  }

  openNew() {
    this.isEdit = false;
    this.form = { title: '', publicationDate: '' };
    this.displayDialog = true;
  }

  openEdit(publication: Publication) {
    this.isEdit = true;
    this.selectedPublication = publication;
    this.form = { title: publication.title, publicationDate: publication.publicationDate };
    this.displayDialog = true;
  }

  save() {
    if (this.isEdit && this.selectedPublication) {
      this.publicationService.update(this.selectedPublication.id, this.form).then(() => {
        this.loadPublications();
        this.displayDialog = false;
      });
    } else {
      this.publicationService.create(this.form).then(() => {
        this.loadPublications();
        this.displayDialog = false;
      });
    }
  }

  confirmDeletePublication(publication: Publication) {
    this.publicationToDelete = publication;
    this.confirmDelete = true;
  }

  deletePublication() {
    if (this.publicationToDelete) {
      this.publicationService.delete(this.publicationToDelete.id).then(() => {
        this.loadPublications();
        this.confirmDelete = false;
        this.publicationToDelete = null;
      });
    }
  }

  onPageChange(event: any) {
    this.page = event.page;
    this.pageSize = event.rows;
    this.loadPublications();
  }
} 