import { MigrationCliPage } from './app.po';

describe('migration-cli App', function() {
  let page: MigrationCliPage;

  beforeEach(() => {
    page = new MigrationCliPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
