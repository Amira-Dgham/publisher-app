import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class LoaderService {
  private activeRequestsCount = 0;
  private readonly isLoadingSubject = new BehaviorSubject<boolean>(false);

  get isLoading$(): Observable<boolean> {
    return this.isLoadingSubject.asObservable();
  }

  start(): void {
    this.activeRequestsCount++;
    if (this.activeRequestsCount === 1) {
      this.isLoadingSubject.next(true);
    }
  }

  stop(): void {
    if (this.activeRequestsCount > 0) {
      this.activeRequestsCount--;
      if (this.activeRequestsCount === 0) {
        this.isLoadingSubject.next(false);
      }
    }
  }

  reset(): void {
    this.activeRequestsCount = 0;
    this.isLoadingSubject.next(false);
  }
} 