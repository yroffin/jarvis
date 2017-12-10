/* 
 * Copyright 2016 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { environment } from '../environments/environment';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { RouterModule, Routes } from '@angular/router';
import { StoreModule } from '@ngrx/store';

import { AppComponent } from './app.component';

/**
 * material2
 */
import { MatSidenavModule } from '@angular/material';
import { MatCheckboxModule } from '@angular/material';
import { MatButtonModule } from '@angular/material';
import { MatGridListModule } from '@angular/material';
import { MatInputModule } from '@angular/material';
import { MatTableModule } from '@angular/material';
import { MatTabsModule } from '@angular/material';
import { MatSelectModule } from '@angular/material';
import { MatOptionModule } from '@angular/material';
import { MatCardModule } from '@angular/material';
import { MatSnackBarModule } from '@angular/material';
import { MatFormFieldModule } from '@angular/material';

/**
 * primeng
 */
import { ButtonModule } from 'primeng/primeng';
import { ChartModule } from 'primeng/primeng';
import { DataTableModule, SharedModule } from 'primeng/primeng';
import { MenubarModule, MenuModule } from 'primeng/primeng';
import { CheckboxModule } from 'primeng/primeng';
import { InputTextModule } from 'primeng/primeng';
import { AccordionModule } from 'primeng/primeng';
import { CodeHighlighterModule } from 'primeng/primeng';
import { InputTextareaModule } from 'primeng/primeng';
import { DataListModule } from 'primeng/primeng';
import { TabViewModule } from 'primeng/primeng';
import { DataGridModule } from 'primeng/primeng';
import { PanelModule } from 'primeng/primeng';
import { GrowlModule } from 'primeng/primeng';
import { MessagesModule } from 'primeng/primeng';
import { StepsModule } from 'primeng/primeng';
import { PanelMenuModule } from 'primeng/primeng';
import { DialogModule } from 'primeng/primeng';
import { FieldsetModule } from 'primeng/primeng';
import { DropdownModule } from 'primeng/primeng';
import { ConfirmDialogModule, ConfirmationService } from 'primeng/primeng';
import { SplitButtonModule } from 'primeng/primeng';
import { ToolbarModule } from 'primeng/primeng';
import { TooltipModule } from 'primeng/primeng';
import { TreeTableModule } from 'primeng/primeng';
import { CalendarModule } from 'primeng/primeng';
import { SpinnerModule } from 'primeng/primeng';
import { SliderModule } from 'primeng/primeng';
import { ToggleButtonModule } from 'primeng/primeng';

import { WindowRef } from './service/jarvis-utils.service';
import { LoggerService } from './service/logger.service';
import { NavigationGuard } from './guard/navigation.service';
import { ProfileGuard } from './guard/profile.service';

import { JarvisCanvasService } from './service/jarvis-canvas.service';
import { JarvisMqttService } from './service/jarvis-mqtt.service';
import { JarvisSecurityService } from './service/jarvis-security.service';
import { JarvisConfigurationService } from './service/jarvis-configuration.service';
import { JarvisDataDeviceService } from './service/jarvis-data-device.service';
import { JarvisDataTriggerService } from './service/jarvis-data-trigger.service';
import { JarvisDataPluginService } from './service/jarvis-data-plugin.service';
import { JarvisDataCommandService } from './service/jarvis-data-command.service';
import { JarvisDataConfigurationService } from './service/jarvis-data-configuration.service';
import { JarvisDataPropertyService } from './service/jarvis-data-property.service';
import { JarvisDataConnectorService } from './service/jarvis-data-connector.service';
import { JarvisDataNotificationService } from './service/jarvis-data-notification.service';
import { JarvisDataCronService } from './service/jarvis-data-cron.service';
import { JarvisDataProcessService } from './service/jarvis-data-process.service';
import { JarvisDataSnapshotService } from './service/jarvis-data-snapshot.service';
import { JarvisDataViewService } from './service/jarvis-data-view.service';
import { JarvisDataRawService } from './service/jarvis-data-raw.service';
import { JarvisDataStoreService } from './service/jarvis-data-store.service';
import { JarvisDataDatasourceService } from './service/jarvis-data-datasource.service';
import { JarvisDataMeasureService } from './service/jarvis-data-measure.service';
import { JarvisDataModelService } from './service/jarvis-data-model.service';
import { JarvisLoaderService } from './service/jarvis-loader.service';
import { JarvisMessageService } from './service/jarvis-message.service';

import { JarvisLayoutDirective } from './directive/jarvis-layout.directive';

import { JarvisHomeComponent } from './component/jarvis-home/jarvis-home.component';
import { JarvisResourcesComponent } from './component/jarvis-resources/jarvis-resources.component';
import { JarvisResourceDeviceComponent } from './component/jarvis-resource-device/jarvis-resource-device.component';
import { JarvisResourcePluginComponent } from './component/jarvis-resource-plugin/jarvis-resource-plugin.component';
import { JarvisResourceCommandComponent } from './component/jarvis-resource-command/jarvis-resource-command.component';
import { JarvisResourceTriggerComponent } from './component/jarvis-resource-trigger/jarvis-resource-trigger.component';
import { JarvisResourceCronComponent } from './component/jarvis-resource-cron/jarvis-resource-cron.component';
import { JarvisResourceConfigurationComponent } from './component/jarvis-resource-configuration/jarvis-resource-configuration.component';
import { JarvisResourceNotificationComponent } from './component/jarvis-resource-notification/jarvis-resource-notification.component';
import { JarvisResourcePropertyComponent } from './component/jarvis-resource-property/jarvis-resource-property.component';
import { JarvisResourceConnectorComponent } from './component/jarvis-resource-connector/jarvis-resource-connector.component'
import { JarvisResourceViewComponent } from './component/jarvis-resource-view/jarvis-resource-view.component'
import { JarvisInlineSvgDirective } from './directive/jarvis-inline-svg.directive';

import { JarvisTileComponent } from './component/jarvis-tile/jarvis-tile.component';
import { JarvisToolbarResourceComponent } from './component/jarvis-toolbar-resource/jarvis-toolbar-resource.component';
import { JarvisPickerComponent } from './dialog/jarvis-picker/jarvis-picker.component';
import { JarvisLoginComponent } from './component/jarvis-login/jarvis-login.component';
import { JarvisResourceSnapshotComponent } from './component/jarvis-resource-snapshot/jarvis-resource-snapshot.component';
import { JarvisDesktopComponent } from './component/jarvis-desktop/jarvis-desktop.component';
import { JarvisResourceDatasourceComponent } from './component/jarvis-resource-datasource/jarvis-resource-datasource.component';
import { JarvisMeasureComponent } from './component/jarvis-measure/jarvis-measure.component';

import { BrokerStore } from './store/broker.store';
import { MessageStore } from './store/message.store';
import { JarvisServerResourcesComponent } from './component/jarvis-server-resources/jarvis-server-resources.component';
import { JarvisBrokerComponent } from './component/jarvis-broker/jarvis-broker.component';
import { JarvisResourceProcessComponent } from './component/jarvis-resource-process/jarvis-resource-process.component';
import { JarvisResourceModelComponent } from './component/jarvis-resource-model/jarvis-resource-model.component';
import { JarvisSceneEditorComponent } from './component/jarvis-scene-editor/jarvis-scene-editor.component';

/**
 * default route definition
 */
const appRoutes: Routes = [
  { path: 'devices', component: JarvisResourcesComponent, canActivate: [ProfileGuard, NavigationGuard], data: { resource: 'devices' } },
  { path: 'devices/:id', component: JarvisResourceDeviceComponent, canActivate: [ProfileGuard] },
  { path: 'plugins', component: JarvisResourcesComponent, canActivate: [ProfileGuard, NavigationGuard], data: { resource: 'plugins' } },
  { path: 'plugins/:id', component: JarvisResourcePluginComponent, canActivate: [ProfileGuard] },
  { path: 'commands', component: JarvisResourcesComponent, canActivate: [ProfileGuard, NavigationGuard], data: { resource: 'commands' } },
  { path: 'commands/:id', component: JarvisResourceCommandComponent, canActivate: [ProfileGuard] },
  { path: 'triggers', component: JarvisResourcesComponent, canActivate: [ProfileGuard, NavigationGuard], data: { resource: 'triggers' } },
  { path: 'triggers/:id', component: JarvisResourceTriggerComponent, canActivate: [ProfileGuard] },
  { path: 'crons', component: JarvisResourcesComponent, canActivate: [ProfileGuard, NavigationGuard], data: { resource: 'crons' } },
  { path: 'crons/:id', component: JarvisResourceCronComponent, canActivate: [ProfileGuard] },
  { path: 'processes', component: JarvisResourcesComponent, canActivate: [ProfileGuard, NavigationGuard], data: { resource: 'processes' } },
  { path: 'processes/:id', component: JarvisResourceProcessComponent, canActivate: [ProfileGuard] },
  { path: 'configurations', component: JarvisResourcesComponent, canActivate: [ProfileGuard, NavigationGuard], data: { resource: 'configurations' } },
  { path: 'configurations/:id', component: JarvisResourceConfigurationComponent, canActivate: [ProfileGuard] },
  { path: 'notifications', component: JarvisResourcesComponent, canActivate: [ProfileGuard, NavigationGuard], data: { resource: 'notifications' } },
  { path: 'notifications/:id', component: JarvisResourceNotificationComponent, canActivate: [ProfileGuard] },
  { path: 'properties', component: JarvisResourcesComponent, canActivate: [ProfileGuard, NavigationGuard], data: { resource: 'properties' } },
  { path: 'properties/:id', component: JarvisResourcePropertyComponent, canActivate: [ProfileGuard] },
  { path: 'connectors', component: JarvisResourcesComponent, canActivate: [ProfileGuard, NavigationGuard], data: { resource: 'connectors' } },
  { path: 'connectors/:id', component: JarvisResourceConnectorComponent, canActivate: [ProfileGuard] },
  { path: 'views', component: JarvisResourcesComponent, canActivate: [ProfileGuard, NavigationGuard], data: { resource: 'views' } },
  { path: 'views/:id', component: JarvisResourceViewComponent, canActivate: [ProfileGuard] },
  { path: 'snapshots', component: JarvisResourcesComponent, canActivate: [ProfileGuard, NavigationGuard], data: { resource: 'snapshots' } },
  { path: 'snapshots/:id', component: JarvisResourceSnapshotComponent, canActivate: [ProfileGuard] },
  { path: 'datasources', component: JarvisResourcesComponent, canActivate: [ProfileGuard, NavigationGuard], data: { resource: 'datasources' } },
  { path: 'datasources/:id', component: JarvisResourceDatasourceComponent, canActivate: [ProfileGuard] },
  { path: 'measures', component: JarvisResourcesComponent, canActivate: [ProfileGuard, NavigationGuard], data: { resource: 'measures' } },
  { path: 'measures/:id', component: JarvisMeasureComponent, canActivate: [ProfileGuard, NavigationGuard] },
  { path: 'models', component: JarvisResourcesComponent, canActivate: [ProfileGuard, NavigationGuard], data: { resource: 'models' } },
  { path: 'models/:id', component: JarvisResourceModelComponent, canActivate: [ProfileGuard, NavigationGuard] },
  { path: 'resources', component: JarvisServerResourcesComponent, canActivate: [ProfileGuard, NavigationGuard] },
  { path: 'broker', component: JarvisBrokerComponent, canActivate: [ProfileGuard, NavigationGuard] },
  { path: 'desktop', component: JarvisDesktopComponent, canActivate: [ProfileGuard] },
  { path: 'login', component: JarvisLoginComponent, canActivate: [NavigationGuard] },
  { path: '', component: JarvisDesktopComponent, canActivate: [ProfileGuard] },
  { path: '**', component: JarvisDesktopComponent, canActivate: [ProfileGuard] }
];

@NgModule({
  declarations: [
    AppComponent,
    JarvisLayoutDirective,
    JarvisHomeComponent,
    JarvisResourceDeviceComponent,
    JarvisResourcesComponent,
    JarvisInlineSvgDirective,
    JarvisTileComponent,
    JarvisToolbarResourceComponent,
    JarvisPickerComponent,
    JarvisResourcePluginComponent,
    JarvisResourceCommandComponent,
    JarvisResourceTriggerComponent,
    JarvisResourceConnectorComponent,
    JarvisResourceCronComponent,
    JarvisResourceConfigurationComponent,
    JarvisResourceNotificationComponent,
    JarvisResourcePropertyComponent,
    JarvisResourceViewComponent,
    JarvisLoginComponent,
    JarvisResourceSnapshotComponent,
    JarvisDesktopComponent,
    JarvisResourceDatasourceComponent,
    JarvisMeasureComponent,
    JarvisServerResourcesComponent,
    JarvisBrokerComponent,
    JarvisResourceProcessComponent,
    JarvisResourceModelComponent,
    JarvisSceneEditorComponent
  ],
  entryComponents: [
    JarvisPickerComponent
  ],
  imports: [
    CommonModule,
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    HttpModule,
    /**
     * material2
     */
    MatSidenavModule,
    MatButtonModule,
    MatGridListModule,
    MatInputModule,
    MatCheckboxModule,
    MatTableModule,
    MatTabsModule,
    MatOptionModule,
    MatSelectModule,
    MatCardModule,
    MatSnackBarModule,
    MatFormFieldModule,
    /**
     * primeface
     */
    DataTableModule,
    SharedModule,
    MenuModule,
    MenubarModule,
    CheckboxModule,
    InputTextModule,
    AccordionModule,
    CodeHighlighterModule,
    InputTextareaModule,
    DataListModule,
    TabViewModule,
    DataGridModule,
    PanelModule,
    GrowlModule,
    MessagesModule,
    StepsModule,
    ButtonModule,
    PanelMenuModule,
    DialogModule,
    FieldsetModule,
    DropdownModule,
    ConfirmDialogModule,
    SplitButtonModule,
    ToolbarModule,
    TooltipModule,
    TreeTableModule,
    ChartModule,
    CalendarModule,
    SpinnerModule,
    SliderModule,
    ToggleButtonModule,
    /**
     * routes
     */
    RouterModule.forRoot(appRoutes, { enableTracing: environment.enableTracing }),
    /**
     * store
     */
    StoreModule.provideStore({
      Broker: BrokerStore.brokerReducer,
      Message: MessageStore.messageReducer
    })
  ],
  providers: [
    /**
     * extends
     */
    WindowRef,
    /**
     * jarvis
     */
    JarvisConfigurationService,
    JarvisSecurityService,
    JarvisDataDeviceService,
    JarvisDataTriggerService,
    JarvisDataPluginService,
    JarvisDataCommandService,
    JarvisDataConfigurationService,
    JarvisDataPropertyService,
    JarvisDataConnectorService,
    JarvisDataCronService,
    JarvisDataNotificationService,
    JarvisDataProcessService,
    JarvisDataStoreService,
    JarvisDataRawService,
    JarvisDataViewService,
    JarvisDataSnapshotService,
    JarvisDataViewService,
    JarvisDataDatasourceService,
    JarvisDataMeasureService,
    JarvisDataModelService,
    JarvisMqttService,
    JarvisCanvasService,
    /**
     * guards
     */
    NavigationGuard,
    ProfileGuard,
    LoggerService,
    JarvisLoaderService,
    JarvisMessageService,
    ConfirmationService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {

}
