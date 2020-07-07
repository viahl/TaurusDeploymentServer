import {Component, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import {FormControl} from "@angular/forms";
import {DeploymentserverBackendFacadeService} from "../../deploymentserver-backend/src/lib/facade/deploymentserver-backend-facade.service";

@Component({
  selector: 'bottom-menu',
  templateUrl: './bottommenu.component.html',
  styleUrls: ['./bottommenu.component.scss']
})
export class BottommenuComponent implements OnInit {
  @Output() updateProjectList: EventEmitter<any> = new EventEmitter();
  dataLoading: boolean = false;
  repo = new FormControl();

  constructor(private dService: DeploymentserverBackendFacadeService) {
  }

  ngOnInit() {
  }

  save() {
    this.dataLoading = true;
    let outputValue;
    this.dService.setupProject(this.repo.value, output => {
      this.updateProjectList.emit(output);
      outputValue = output;
      this.dataLoading = false;
    });
  }

  onChangeUrl() {
    console.log("onChangeUrl", this.repo.value);
  }
}
