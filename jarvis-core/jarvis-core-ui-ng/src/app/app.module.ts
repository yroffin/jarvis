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

import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { TreeModule } from 'angular2-tree-component';

import { ButtonModule } from 'primeng/primeng';
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
import { StepsModule } from 'primeng/primeng';
import { PanelMenuModule } from 'primeng/primeng';
import { DialogModule } from 'primeng/primeng';
import { FieldsetModule } from 'primeng/primeng';
import { DropdownModule } from 'primeng/primeng';
import { ConfirmDialogModule, ConfirmationService } from 'primeng/primeng';

import { MaterialModule } from '@angular/material';

import { JarvisDialogComponent } from './jarvis-dialog/jarvis-dialog.component';
import { JarvisConfigurationService } from './service/jarvis-configuration.service';
import { JarvisDataDeviceService } from './service/jarvis-data-device.service';
import { JarvisDataTriggerService } from './service/jarvis-data-trigger.service';
import { JarvisDataPluginService } from './service/jarvis-data-plugin.service';
import { JarvisDataCommandService } from './service/jarvis-data-command.service';
import { JarvisDataNotificationService } from './service/jarvis-data-notification.service';
import { JarvisDataCronService } from './service/jarvis-data-cron.service';
import { JarvisDataStoreService } from './service/jarvis-data-store.service';

import { JarvisHomeComponent } from './component/jarvis-home/jarvis-home.component';
import { JarvisToolbarComponent } from './component/jarvis-toolbar/jarvis-toolbar.component';
import { JarvisResourcesComponent } from './component/jarvis-resources/jarvis-resources.component';
import { JarvisResourceDeviceComponent } from './component/jarvis-resource-device/jarvis-resource-device.component';
import { JarvisResourcePluginComponent } from './component/jarvis-resource-plugin/jarvis-resource-plugin.component';
import { JarvisResourceCommandComponent } from './component/jarvis-resource-command/jarvis-resource-command.component';
import { JarvisResourceTriggerComponent } from './component/jarvis-resource-trigger/jarvis-resource-trigger.component';
import { JarvisResourceCronComponent } from './component/jarvis-resource-cron/jarvis-resource-cron.component';
import { JarvisResourceScenarioComponent } from './component/jarvis-resource-scenario/jarvis-resource-scenario.component';
import { JarvisResourceBlockComponent } from './component/jarvis-resource-block/jarvis-resource-block.component';
import { JarvisResourceConfigurationComponent } from './component/jarvis-resource-configuration/jarvis-resource-configuration.component';
import { JarvisResourcePropertyComponent } from './component/jarvis-resource-property/jarvis-resource-property.component';
import { JarvisResourceConnectorComponent } from './component/jarvis-resource-connector/jarvis-resource-connector.component'
import { JarvisResourceViewComponent } from './component/jarvis-resource-view/jarvis-resource-view.component'
import { JarvisLayoutDirective } from './directive/jarvis-layout.directive';

import { JarvisTileComponent } from './component/jarvis-tile/jarvis-tile.component';
import { JarvisToolbarMenuComponent } from './component/jarvis-toolbar-menu/jarvis-toolbar-menu.component';
import { JarvisPickerComponent } from './dialog/jarvis-picker/jarvis-picker.component';

/**
 * default route definition
 */
const appRoutes: Routes = [
  { path: 'devices', component: JarvisResourcesComponent, data: { resource: 'devices' } },
  { path: 'devices/:id', component: JarvisResourceDeviceComponent },
  { path: 'plugins', component: JarvisResourcesComponent, data: { resource: 'plugins' } },
  { path: 'plugins/:id', component: JarvisResourcePluginComponent },
  { path: 'commands', component: JarvisResourcesComponent, data: { resource: 'commands' } },
  { path: 'commands/:id', component: JarvisResourceCommandComponent },
  { path: 'triggers', component: JarvisResourcesComponent, data: { resource: 'triggers' } },
  { path: 'triggers/:id', component: JarvisResourceTriggerComponent },
  { path: 'crons', component: JarvisResourcesComponent, data: { resource: 'crons' } },
  { path: 'crons/:id', component: JarvisResourceTriggerComponent },
  { path: 'scenarios', component: JarvisResourcesComponent, data: { resource: 'scenarios' } },
  { path: 'scenarios/:id', component: JarvisResourceScenarioComponent },
  { path: 'blocks', component: JarvisResourcesComponent, data: { resource: 'blocks' } },
  { path: 'blocks/:id', component: JarvisResourceScenarioComponent },
  { path: 'configurations', component: JarvisResourcesComponent, data: { resource: 'configurations' } },
  { path: 'configurations/:id', component: JarvisResourceConfigurationComponent },
  { path: 'properties', component: JarvisResourcesComponent, data: { resource: 'properties' } },
  { path: 'properties/:id', component: JarvisResourcePropertyComponent },
  { path: 'connectors', component: JarvisResourcesComponent, data: { resource: 'connectors' } },
  { path: 'connectors/:id', component: JarvisResourceConnectorComponent },
  { path: 'views', component: JarvisResourcesComponent, data: { resource: 'views' } },
  { path: 'views/:id', component: JarvisResourceViewComponent },
  { path: '', component: JarvisHomeComponent },
  { path: '**', component: JarvisHomeComponent }
];

@NgModule({
  declarations: [
    AppComponent,
    JarvisDialogComponent,
    JarvisHomeComponent,
    JarvisToolbarComponent,
    JarvisResourceDeviceComponent,
    JarvisResourcesComponent,
    JarvisLayoutDirective,
    JarvisTileComponent,
    JarvisToolbarMenuComponent,
    JarvisPickerComponent,
    JarvisResourcePluginComponent,
    JarvisResourceCommandComponent,
    JarvisResourceTriggerComponent,
    JarvisResourceConnectorComponent,
    JarvisResourceCronComponent,
    JarvisResourceScenarioComponent,
    JarvisResourceBlockComponent,
    JarvisResourceConfigurationComponent,
    JarvisResourcePropertyComponent,
    JarvisResourceViewComponent
  ],
  entryComponents: [
    JarvisPickerComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    TreeModule,
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
    StepsModule,
    ButtonModule,
    PanelMenuModule,
    DialogModule,
    FieldsetModule,
    DropdownModule,
    ConfirmDialogModule,
    /**
     * load all materials
     */
    MaterialModule.forRoot(),
    /**
     * routes
     */
    RouterModule.forRoot(appRoutes)
  ],
  providers: [
    JarvisConfigurationService,
    JarvisDataDeviceService,
    JarvisDataTriggerService,
    JarvisDataPluginService,
    JarvisDataCommandService,
    JarvisDataCronService,
    JarvisDataNotificationService,
    JarvisDataStoreService,
    /**
     * primeng
     */
    ConfirmationService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
  
}
