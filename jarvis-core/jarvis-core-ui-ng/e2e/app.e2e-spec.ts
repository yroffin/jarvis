import { JarvisCoreUiNgPage } from './app.po';

describe('jarvis-core-ui-ng App', function() {
  let page: JarvisCoreUiNgPage;

  beforeEach(() => {
    page = new JarvisCoreUiNgPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
