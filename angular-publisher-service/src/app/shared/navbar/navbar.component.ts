import { Component } from '@angular/core';
import { MenubarModule } from 'primeng/menubar';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
  standalone: true,
  imports: [MenubarModule]
})
export class NavbarComponent {
  items = [
    { label: 'Books', routerLink: '/books' },
    { label: 'Magazines', routerLink: '/magazines' },
    { label: 'Authors', routerLink: '/authors' },
    { label: 'Publications', routerLink: '/publications' },
  ];
}