import { Component } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { ButtonModule } from "primeng/button";
import { NavbarComponent } from './shared/navbar/navbar.component';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { LoaderComponent } from './shared/loader/loader.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: true,
  imports: [RouterOutlet, ButtonModule, NavbarComponent, ToastModule, LoaderComponent
  ],
  styleUrl: './app.component.css',
  providers: [MessageService]
})
export class AppComponent {
  title = 'angular-publisher-service';

  constructor(private router: Router) {}

  onSearchPublication(title: string) {
    if (title) {
      this.router.navigate(['/publications'], { queryParams: { title } });
    }
  }
}
 