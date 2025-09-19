import { Component, OnInit, ViewChild } from '@angular/core';
import { Magazine, MagazineCreateRequest } from '../../core/models/magazine.model';
import { MagazineService } from '../../core/services/magazine.service';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { SharedModule } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { TableModule } from 'primeng/table';
import { ConfirmDialogComponent } from '../../shared/confirm-dialog/confirm-dialog.component';
import { PaginatorModule } from 'primeng/paginator';
import { MagazinePageData } from '../../core/types/magazine-page.model';
import { TEST_IDS } from '../../core/constants/test-ids.constants';

@Component({
  selector: 'app-magazines',
  templateUrl: './magazines.component.html',
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
  styleUrls: ['./magazines.component.css'],
})
export class MagazinesComponent implements OnInit {
    @ViewChild('magazineForm') magazineForm!: NgForm;
  magazines: Magazine[] = [];
  selectedMagazine: Magazine | null = null;
  displayDialog = false;
  isEdit = false;
  confirmDelete = false;
  magazineToDelete: Magazine | null = null;
  loading = false;
  page = 0;
  pageSize = 10;
  totalRecords = 0;
  sort: 'ASC' | 'DESC' = 'DESC';
  TEST_IDS = TEST_IDS;

  form: MagazineCreateRequest = { title: '', publicationDate: '', issueNumber: 0, authorIds: [] };

  constructor(private magazineService: MagazineService) {}

  ngOnInit() {
    this.loadMagazines();
  }

  loadMagazines() {
    this.loading = true;
    this.magazineService.getAll(this.page, this.pageSize, this.sort).then(res => {
      const pageData: MagazinePageData = res.data.data;
      this.magazines = pageData?.content || [];
      this.totalRecords = pageData?.totalElements || 0;
      this.loading = false;
    }).catch(() => this.loading = false);
  }

  openNew() {
    this.isEdit = false;
    this.form = { title: '', publicationDate: '', issueNumber: 0, authorIds: [] };
    this.displayDialog = true;
  }

  openEdit(magazine: Magazine) {
    this.isEdit = true;
    this.selectedMagazine = magazine;
    this.form = { title: magazine.title, publicationDate: magazine.publicationDate, issueNumber: magazine.issueNumber, authorIds: magazine.authors.map(a => a.id) };
    this.displayDialog = true;
  }
save() {


  let formToSend = { ...this.form };

  if (formToSend.authorIds && typeof formToSend.authorIds === 'string') {
    formToSend.authorIds = (formToSend.authorIds as string)
      .split(',')
      .map((id: string) => Number(id.trim()))
      .filter((id: number) => !isNaN(id));
  }

  if (this.isEdit && this.selectedMagazine) {
    this.magazineService.update(this.selectedMagazine.id, formToSend).then(() => {
      this.loadMagazines();
      this.displayDialog = false;
    });
  } else {
    this.magazineService.create(formToSend).then(() => {
      this.loadMagazines();
      this.displayDialog = false;
    });
  }
}

  confirmDeleteMagazine(magazine: Magazine) {
    this.magazineToDelete = magazine;
    this.confirmDelete = true;
  }

  deleteMagazine() {
    if (this.magazineToDelete) {
      this.magazineService.delete(this.magazineToDelete.id).then(() => {
        this.loadMagazines();
        this.confirmDelete = false;
        this.magazineToDelete = null;
      });
    }
  }

  onPageChange(event: any) {
    this.page = event.page;
    this.pageSize = event.rows;
    this.loadMagazines();
  }
} 