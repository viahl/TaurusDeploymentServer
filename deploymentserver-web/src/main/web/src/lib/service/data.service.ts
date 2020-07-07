import {Injectable} from "@angular/core";
import {BehaviorSubject} from "rxjs";
import {Project} from "../deploymentserver-backend/src/lib/generated";

@Injectable()
export class DataService {
  private messageSource = new BehaviorSubject("default message");
  currentMessage = this.messageSource.asObservable();

  private projectList = new BehaviorSubject({});
  currentProjectList = this.projectList.asObservable();

  constructor() {
  }

  changeMessage(message: string) {
    this.messageSource.next(message);
  }

  changeProject(project: Project) {
    this.projectList.next(project);
  }
}
