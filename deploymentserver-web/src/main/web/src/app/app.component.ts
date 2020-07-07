import {
  AfterContentChecked,
  Component,
  EventEmitter,
  OnInit,
  Output,
} from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})

export class AppComponent implements OnInit, AfterContentChecked {
  title = 'web';
  @Output() updateCenterLayoutComponent: EventEmitter<any> = new EventEmitter();

  constructor() {
  }

  ngAfterContentChecked(): void {
  }

  updateProject(event) {
    console.log(event)
    this.updateCenterLayoutComponent.emit(event);
  }

  ngOnInit(): void {
  }
}
