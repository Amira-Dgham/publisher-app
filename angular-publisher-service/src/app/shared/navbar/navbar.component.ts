import { Component, Output, EventEmitter } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import { MenubarModule } from 'primeng/menubar';
import { ButtonModule } from 'primeng/button';
import { TEST_IDS } from '../../core/constants/test-ids.constants';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
  standalone: true,
  imports: [FormsModule, InputTextModule, MenubarModule, ButtonModule]
})
export class NavbarComponent {
  items = [
    { label: 'Books', routerLink: '/books' },
    { label: 'Magazines', routerLink: '/magazines' },
    { label: 'Authors', routerLink: '/authors' },
    { label: 'Publications', routerLink: '/publications' },
  ];

  searchTitle = '';
  @Output() searchPublication = new EventEmitter<string>();
  TEST_IDS = TEST_IDS;

  onSearch() {
    this.searchPublication.emit(this.searchTitle.trim());
  }
}