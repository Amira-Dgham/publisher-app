import { Component } from '@angular/core';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import {  AsyncPipe } from '@angular/common';
import { LoaderService } from '../../core/services/loader.service';
import { TEST_IDS } from '../../core/constants/test-ids.constants';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-loader',
  standalone: true,
  templateUrl: './loader.component.html',
  styleUrls: ['./loader.component.css'],
  imports: [ProgressSpinnerModule, AsyncPipe]
})
export class LoaderComponent {
  TEST_IDS = TEST_IDS;
  isLoading$: Observable<boolean>;

  constructor(private loaderService: LoaderService) {
    this.isLoading$ = this.loaderService.isLoading$;
  }
} 