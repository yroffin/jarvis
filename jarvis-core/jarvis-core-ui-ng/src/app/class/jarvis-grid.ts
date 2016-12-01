import { JarvisConfigurationService } from '../service/jarvis-configuration.service';

export class JarvisGrid {
    fixedCols = 1;
    fixedRowHeight = 100;

    _jarvisConfiguration: JarvisConfigurationService;

    constructor(
        _jarvisConfigurationService: JarvisConfigurationService
    ) {
        this._jarvisConfiguration = _jarvisConfigurationService;
        this.responsive(window.innerHeight);
    }

    protected responsive(innerWidth: number): void {
        switch (this._jarvisConfiguration.Layout(innerWidth)) {
            case this._jarvisConfiguration.layoutXs:
                this.fixedCols = 2;
                break;
            case this._jarvisConfiguration.layoutSm:
                this.fixedCols = 3;
                break;
            case this._jarvisConfiguration.layoutMd:
                this.fixedCols = 9;
                break;
            case this._jarvisConfiguration.layoutLg:
                this.fixedCols = 8;
                break;
            case this._jarvisConfiguration.layoutXl:
                this.fixedCols = 12;
                break;
        }
    }
}
