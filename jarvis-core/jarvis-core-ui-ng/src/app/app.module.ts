import 'hammerjs';

import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';

import { MdCoreModule } from '@angular2-material/core';
import { MdButtonToggleModule } from '@angular2-material/button-toggle';
import { MdCheckboxModule } from '@angular2-material/checkbox';
import { MdButtonModule } from '@angular2-material/button';
import { MdRadioModule } from '@angular2-material/radio';
import { MdSlideToggleModule } from '@angular2-material/slide-toggle';
import { MdSliderModule } from '@angular2-material/slider';
import { MdSidenavModule } from '@angular2-material/sidenav';
import { MdListModule } from '@angular2-material/list';
import { MdGridListModule} from '@angular2-material/grid-list';
import { MdInputModule } from '@angular2-material/input';
import { MdTabsModule } from '@angular2-material/tabs';
import { MdCardModule } from '@angular2-material/card/index';
import { MdToolbarModule } from '@angular2-material/toolbar';
import { MdIconModule, MdIconRegistry } from '@angular2-material/icon';
import { MdProgressCircleModule } from '@angular2-material/progress-circle';
import { MdProgressBarModule } from '@angular2-material/progress-bar';
import { MdTooltipModule } from '@angular2-material/tooltip';
import { MdMenuModule } from '@angular2-material/menu';

import { JarvisDialogComponent } from './jarvis-dialog/jarvis-dialog.component';
import { JarvisConfigurationService } from './service/jarvis-configuration.service';
import { JarvisDataDeviceService } from './service/jarvis-data-device.service';
import { JarvisDataStoreService } from './service/jarvis-data-store.service';
import { JarvisHomeComponent } from './component/jarvis-home/jarvis-home.component';
import { JarvisTilesViewComponent } from './component/jarvis-tiles-view/jarvis-tiles-view.component';
import { JarvisToolbarComponent } from './component/jarvis-toolbar/jarvis-toolbar.component';
import { JarvisResourceDeviceComponent } from './component/jarvis-resource-device/jarvis-resource-device.component';
import { JarvisResourcesComponent } from './component/jarvis-resources/jarvis-resources.component';
import { JarvisResourceDeviceGeneralComponent } from './component/jarvis-resource-device/jarvis-resource-device-general/jarvis-resource-device-general.component';
import { JarvisResourceDevicePluginComponent } from './component/jarvis-resource-device/jarvis-resource-device-plugin/jarvis-resource-device-plugin.component';
import { JarvisResourceDeviceRenderComponent } from './component/jarvis-resource-device/jarvis-resource-device-render/jarvis-resource-device-render.component';
import { JarvisLayoutDirective } from './directive/jarvis-layout.directive';

import { HighlightJsModule, HighlightJsService } from '../../node_modules/angular2-highlight-js';
import { JarvisTileComponent } from './component/jarvis-tile/jarvis-tile.component';
import { JarvisToolbarMenuComponent } from './component/jarvis-toolbar-menu/jarvis-toolbar-menu.component'

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
    JarvisToolbarMenuComponent
  ],
  imports: [
    BrowserModule,
    HighlightJsModule,
    FormsModule,
    HttpModule,
    MdCoreModule,
    MdButtonToggleModule,
    MdCheckboxModule,
    MdButtonModule,
    MdRadioModule,
    MdSlideToggleModule,
    MdSliderModule,
    MdSidenavModule,
    MdListModule,
    MdGridListModule,
    MdInputModule,
    MdTabsModule,
    MdCardModule,
    MdToolbarModule,
    MdIconModule,
    MdProgressCircleModule,
    MdProgressBarModule,
    MdTooltipModule,
    MdMenuModule,
    /**
     * routes
     */
    RouterModule.forRoot(appRoutes)
  ],
  providers: [
    HighlightJsService,
    MdIconRegistry,
    JarvisConfigurationService,
    JarvisDataDeviceService,
    JarvisDataStoreService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
  
}
