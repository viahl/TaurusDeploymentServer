import {NgModule} from '@angular/core';
import {BottommenuComponent} from "./bottommeny/bottommenu.component";
import {
  MatButtonModule,
  MatFormFieldModule,
  MatInputModule,
  MatOptionModule, MatSelectModule,
  MatTableModule,
} from "@angular/material";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {CenterLayoutComponent} from "./center-layout/center-layout.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {DeploymentserverBackendFacadeService} from "../deploymentserver-backend/src/lib/facade/deploymentserver-backend-facade.service";
import {LoadingComponent} from "./loading.component";


@NgModule({
  imports: [
    BrowserAnimationsModule,
    FormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatOptionModule,
    MatSelectModule,
    MatTableModule,
    ReactiveFormsModule,
  ],
  declarations: [
    BottommenuComponent,
    CenterLayoutComponent,
    LoadingComponent
  ],
  providers: [
    DeploymentserverBackendFacadeService,

  ],
  exports: [
    BottommenuComponent,
    CenterLayoutComponent,
    MatFormFieldModule,
    MatOptionModule,
    MatSelectModule,
  ]
})

export class PresentationModule {
}
