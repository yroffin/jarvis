import 'hammerjs';

import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { TreeModule } from 'angular2-tree-component';

import { MaterialModule } from '@angular/material';

import { JarvisDialogComponent } from './jarvis-dialog/jarvis-dialog.component';
import { JarvisConfigurationService } from './service/jarvis-configuration.service';
import { JarvisDataDeviceService } from './service/jarvis-data-device.service';
import { JarvisDataTriggerService } from './service/jarvis-data-trigger.service';
import { JarvisDataPluginService } from './service/jarvis-data-plugin.service';
import { JarvisDataStoreService } from './service/jarvis-data-store.service';
import { JarvisHomeComponent } from './component/jarvis-home/jarvis-home.component';
import { JarvisTilesViewComponent } from './component/jarvis-tiles-view/jarvis-tiles-view.component';
import { JarvisToolbarComponent } from './component/jarvis-toolbar/jarvis-toolbar.component';
import { JarvisResourcesComponent } from './component/jarvis-resources/jarvis-resources.component';
import { JarvisResourceDeviceComponent, PizzaDialog } from './component/jarvis-resource-device/jarvis-resource-device.component';
import { JarvisResourceDeviceGeneralComponent } from './component/jarvis-resource-device/jarvis-resource-device-general/jarvis-resource-device-general.component';
import { JarvisResourceDevicePluginComponent } from './component/jarvis-resource-device/jarvis-resource-device-plugin/jarvis-resource-device-plugin.component';
import { JarvisResourceDeviceRenderComponent } from './component/jarvis-resource-device/jarvis-resource-device-render/jarvis-resource-device-render.component';
import { JarvisLayoutDirective } from './directive/jarvis-layout.directive';

import { HighlightJsModule, HighlightJsService } from '../../node_modules/angular2-highlight-js';
import { JarvisTileComponent } from './component/jarvis-tile/jarvis-tile.component';
import { JarvisToolbarMenuComponent } from './component/jarvis-toolbar-menu/jarvis-toolbar-menu.component';
import { JarvisPickerComponent } from './dialog/jarvis-picker/jarvis-picker.component'

const appRoutes: Routes = [
  { path: 'devices', component: JarvisResourcesComponent, data: { resource: 'devices' } },
  { path: 'devices/:id', component: JarvisResourceDeviceComponent },
  { path: '', component: JarvisHomeComponent },
  { path: '**', component: JarvisHomeComponent }
];

@NgModule({
  declarations: [
    AppComponent,
    JarvisDialogComponent,
    JarvisHomeComponent,
    JarvisTilesViewComponent,
    JarvisToolbarComponent,
    JarvisResourceDeviceComponent,
    JarvisResourcesComponent,
    JarvisResourceDeviceGeneralComponent,
    JarvisResourceDevicePluginComponent,
    JarvisResourceDeviceRenderComponent,
    JarvisLayoutDirective,
    JarvisTileComponent,
    JarvisToolbarMenuComponent,
    JarvisPickerComponent
  ],
  entryComponents: [
    JarvisPickerComponent
  ],
  imports: [
    BrowserModule,
    HighlightJsModule,
    FormsModule,
    HttpModule,
    TreeModule,
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
    HighlightJsService,
    JarvisConfigurationService,
    JarvisDataDeviceService,
    JarvisDataTriggerService,
    JarvisDataPluginService,
    JarvisDataStoreService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
  
}
