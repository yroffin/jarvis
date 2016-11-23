import 'hammerjs';

import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

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

@NgModule({
  declarations: [
    AppComponent,
    JarvisDialogComponent,
    JarvisHomeComponent,
    JarvisTilesViewComponent,
    JarvisToolbarComponent
  ],
  imports: [
    BrowserModule,
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
    MdMenuModule
  ],
  providers: [
    MdIconRegistry,
    JarvisConfigurationService,
    JarvisDataDeviceService,
    JarvisDataStoreService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
  
}
