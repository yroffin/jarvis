import { Directive, Input, Inject, ElementRef } from '@angular/core';
import { DOCUMENT } from '@angular/platform-browser';
import { JarvisDataRawService } from '../service/jarvis-data-raw.service';

@Directive({
  selector: '[inlineSvg]'
})
export class JarvisInlineSvgDirective {
  @Input() resourceUrl: string;

  /**
   * constructor
   */
  constructor(
    /**
     * DOM manipulation
     */
    @Inject(DOCUMENT) private _document /*: HTMLDocument*/,
    private _el: ElementRef,
    private _svc: JarvisDataRawService
  ) {

  }

  /**
   * init
   */
  ngOnInit(): void {
    this.reload();
  }

  /**
   * reload
   */
  reload(): void {
    /**
     * get all resource
     */
    let svg: string;
    this._svc.RawGet(this.resourceUrl)
      .subscribe(
      (data: any) => svg = data,
      error => console.log(error),
      () => {
        this._el.nativeElement.innerHTML = svg;
      }
      );
  }
}
