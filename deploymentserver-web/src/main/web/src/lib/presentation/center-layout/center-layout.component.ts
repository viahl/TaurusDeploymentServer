import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {DeploymentserverBackendFacadeService} from "../../deploymentserver-backend/src/lib/facade/deploymentserver-backend-facade.service";
import {interval} from "rxjs";

@Component({
  selector: 'center-layout',
  templateUrl: './center-layout.component.html',
  styleUrls: ['./center-layout.component.scss']
})
export class CenterLayoutComponent implements OnInit, OnChanges {
  dataSource;
  projectChanges: boolean = false;
  @Input() projectInput;

  constructor(private dService: DeploymentserverBackendFacadeService) {
    this.dService.fetchAllProjects(deliveryValue => {
      this.dataSource = deliveryValue;
      for (let project of this.dataSource) {
        this.ping(project.ping, project, false);
        this.ping(project.dbPing, project, true);
      }
      console.log("Project List", deliveryValue);
    });
  }

  //displayedColumns: string[] = ['name', 'version', 'status', 'statusDb', 'branches', 'Deploy', 'Remove'];
  displayedColumns: string[] = ['name', 'version', 'status', 'statusDb', 'Deploy', 'Remove'];

  ngOnInit() {
    interval(10000).subscribe(x => {
      for (let project of this.dataSource) {
        this.ping(project.ping, project, false);
        this.ping(project.dbPing, project, true);
      }
    })
  }

  ngOnDestroy() {
  }

  ping(pingUrl, element, isService) {
    this.dService.pingURL(pingUrl, pong => {
      if (isService) {
        element.pingDbStatus = pong;
      } else {
        element.pingStatus = pong;
      }
    });
  }

  deploy(row) {
    this.projectChanges = true;
    let outputValue;
    console.log("I denna metoden så måste man hantera vilket som är den valda branchen. " +
      "Om ingen branch är vald så skall det blir master. Men det hanterar backend tjänsten");
    this.dService.deployProject(row.gitURL, "master", output => {
      outputValue = output;
      this.dService.fetchAllProjects(deliveryValue => {
        this.dataSource = deliveryValue;
        this.projectChanges = false;
      });
    });
  }

  remove(row) {
    this.projectChanges = true;
    let outputValue;
    this.dService.removeProject(row.gitURL, output => {
      outputValue = output;
      this.dService.fetchAllProjects(deliveryValue => {
        this.dataSource = deliveryValue;
        this.projectChanges = false;
      });
    })
  }

  updateProjectList(event) {
    this.dService.fetchAllProjects(deliveryValue => {
      this.dataSource = deliveryValue;
      console.log("Update Project List", deliveryValue);
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.dataSource = changes.projectInput.currentValue;
  }
}
