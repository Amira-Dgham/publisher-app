import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { MenubarModule } from 'primeng/menubar';
import { RouterModule } from '@angular/router';
import { ConfirmDialogComponent } from './confirm-dialog/confirm-dialog.component';
import { NavbarComponent } from './navbar/navbar.component';

@NgModule({
  imports: [
    CommonModule,
    DialogModule,
    ButtonModule,
    MenubarModule,
    RouterModule,
    ConfirmDialogComponent,
    NavbarComponent
  ],
  exports: [
    DialogModule,
    ButtonModule,
    MenubarModule,
    RouterModule,
    ConfirmDialogComponent,
    NavbarComponent
  ]
})
export class SharedModule {} 