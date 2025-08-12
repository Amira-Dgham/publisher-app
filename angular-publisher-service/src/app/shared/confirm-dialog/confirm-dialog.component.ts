import { Component, Input, Output, EventEmitter } from '@angular/core';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { TEST_IDS } from '../../core/constants/test-ids.constants';

@Component({
  selector: 'app-confirm-dialog',
  templateUrl: './confirm-dialog.component.html',
  standalone: true,
  imports: [DialogModule, ButtonModule]
})
export class ConfirmDialogComponent {
  @Input() visible = false;
  @Input() message = '';
  @Output() accept = new EventEmitter<void>();
  @Output() reject = new EventEmitter<void>();

  TEST_IDS = TEST_IDS;

  onAccept() {
    this.accept.emit();
  }

  onReject() {
    this.reject.emit();
  }
} 