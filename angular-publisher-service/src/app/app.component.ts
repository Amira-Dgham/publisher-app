import { Component } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { ButtonModule } from "primeng/button";
import { NavbarComponent } from './shared/navbar/navbar.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: true,
  imports: [RouterOutlet, ButtonModule, NavbarComponent],
  styleUrl: './app.component.css'
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
 