import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { TreeModule } from 'angular2-tree-component';

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
import { JarvisResourceDeviceComponent } from './component/jarvis-resource-device/jarvis-resource-device.component';
import { JarvisLayoutDirective } from './directive/jarvis-layout.directive';

import { HighlightJsModule, HighlightJsService } from '../../node_modules/angular2-highlight-js';
import { JarvisTileComponent } from './component/jarvis-tile/jarvis-tile.component';
import { JarvisToolbarMenuComponent } from './component/jarvis-toolbar-menu/jarvis-toolbar-menu.component';
import { JarvisPickerComponent } from './dialog/jarvis-picker/jarvis-picker.component'

/**
 * default route definition
 */
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
