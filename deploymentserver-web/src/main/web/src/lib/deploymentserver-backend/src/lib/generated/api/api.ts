export * from './homeController.service';
import { HomeControllerService } from './homeController.service';
export * from './projectRest.service';
import { ProjectRestService } from './projectRest.service';
export const APIS = [HomeControllerService, ProjectRestService];
