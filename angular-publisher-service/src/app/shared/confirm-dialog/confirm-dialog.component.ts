import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';

@Component({
  selector: 'app-confirm-dialog',
  templateUrl: './confirm-dialog.component.html',
  standalone: true,

  imports: [DialogModule, ButtonModule],

})
export class ConfirmDialogComponent {
  @Input() visible = false;
  @Input() message = 'Are you sure?';
  @Output() accept = new EventEmitter<void>();
  @Output() reject = new EventEmitter<void>();

  onAccept() {
    this.accept.emit();
  }

  onReject() {
    this.reject.emit();
  }
} 