import {Injectable} from "@angular/core";
import {ProjectRestService} from "../generated";
import {HttpClient} from "@angular/common/http";

@Injectable()
export class DeploymentserverBackendFacadeService {

  constructor(private projectService: ProjectRestService,
              private httpClient: HttpClient) {
  }

  /**
   * Returns all project that is register in the DeploymentServer
   * @param callback
   */
  public fetchAllProjects(incommingRequest): void {
    this.projectService.getProjectsUsingGET().subscribe(body => {
      incommingRequest(body);
    })
  }

  /**
   * Deploy an project. If it is already deploy, it undeploy and then deploy the project.
   *
   * @param gitUrl the URL to the repository is located
   * @param callback
   */
  public deployProject(gitUrl: string, branch: string, callback): void {
    this.projectService.deployProjectUsingGET(encodeURIComponent(gitUrl), encodeURIComponent(branch)).subscribe(body => {
      callback(body);
    })
  }

  /**
   * Call ping to an specific URL. If the answer back is ping. Then the result is good.
   * @param pingURL the URL to where the ping query should be sent to.
   * @param pong The answer back from the ping service.
   */
  public pingURL(pingurl: string, pong): void {
    let serverError;

    this.httpClient.get(`${pingurl}?ping=ping`)
      .subscribe(ping => {
          if (ping) {
            pong("GREEN");
          }
        }
        , error => {
          console.log("Error ", error);
          if (error.error instanceof ErrorEvent) {
            pong("YELLOW");
          } else {
            pong("RED");
          }
        });
  }

  /**
   * The setupProject takes the URL to the git repository. Then download the source code to
   * the disksystem. When this is down, the "new" project is visible in the GUI project table.
   * @param gitUrl
   * @param callback
   */
  public setupProject(gitUrl: string, incommingTabellRequest):
    void {
    this.projectService.registerProjectUsingGET(encodeURIComponent(gitUrl)).subscribe(body => {
      incommingTabellRequest(body);
    })
    ;
  }

  /**
   * RemoveProject dismantel the project from Docker and remove the GIT url from the server.
   * That mean it should be a full clean up of the project.
   * @param gitUrl the URL to identify the project with.
   * @param incommingTabellRequest
   */
  public removeProject(gitUrl: string, incommingTabellRequest):
    void {
    this.projectService.removeProjectUsingGET(encodeURIComponent(gitUrl)).subscribe(toBeRemoved => {
      incommingTabellRequest(toBeRemoved);
    })
  }
}
