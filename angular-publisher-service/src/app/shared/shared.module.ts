import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConfirmDialogComponent } from './confirm-dialog/confirm-dialog.component';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';

@NgModule({
  declarations: [ConfirmDialogComponent],
  imports: [CommonModule, DialogModule, ButtonModule],
  exports: [ConfirmDialogComponent],
})
export class SharedModule {} 