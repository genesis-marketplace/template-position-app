import { Navigation } from '@genesislcap/foundation-header';
import { getApp } from '@genesislcap/foundation-shell/app';
import { FoundationRouter, foundationSwitch } from '@genesislcap/foundation-ui';
import { assureDesignSystem, DesignSystemModule, ResourceType } from '@genesislcap/foundation-utils';
import { allComponents, provideFASTDesignSystem } from '@microsoft/fast-components';
import { logger } from '../utils';
import { zeroGridComponents } from '@genesislcap/foundation-zero-grid-pro';
import { g2plotChartsComponents } from '@genesislcap/g2plot-chart';
import { ErrorBoundary, FlexLayout, provideDesignSystem } from '@genesislcap/foundation-zero';
import { ModuleRegistry } from '@ag-grid-community/core';
import { LicenseManager } from '@ag-grid-enterprise/core';
import { RowGroupingModule } from '@ag-grid-enterprise/row-grouping';
import { SetFilterModule } from '@ag-grid-enterprise/set-filter';
import { ColumnsToolPanelModule } from '@ag-grid-enterprise/column-tool-panel';
import { FiltersToolPanelModule } from '@ag-grid-enterprise/filter-tool-panel';
import { SideBarModule } from '@ag-grid-enterprise/side-bar';
import { ServerSideRowModelModule } from '@ag-grid-enterprise/server-side-row-model';
import { EntityManagement } from '@genesislcap/foundation-entity-management';
import { Filters, Form } from '@genesislcap/foundation-forms';
import { foundationLayoutComponents } from '@genesislcap/foundation-layout';
import { CriteriaSegmentedControl } from '@genesislcap/foundation-criteria';
import { interopFoundationNotificationListener } from '@genesislcap/foundation-fdc3';

/**
 * Ensure tree shaking doesn't remove these.
 */
FoundationRouter;
Navigation;
EntityManagement;
Form;
FlexLayout;
ErrorBoundary;
Filters;
CriteriaSegmentedControl;

const licenseKey =
  'CompanyName=GENESIS GLOBAL TECHNOLOGY LIMITED,LicensedGroup=Genesis Web,LicenseType=MultipleApplications,LicensedConcurrentDeveloperCount=3,LicensedProductionInstancesCount=4,AssetReference=AG-035099,SupportServicesEnd=8_January_2024_[v2]_MTcwNDY3MjAwMDAwMA==da697ff647e3d57eb719529bdf1506f0'

ModuleRegistry.registerModules([
  ServerSideRowModelModule,
  RowGroupingModule,
  SideBarModule,
  SetFilterModule,
  ColumnsToolPanelModule,
  FiltersToolPanelModule
]);
LicenseManager.setLicenseKey(licenseKey);


/**
 * zeroDesignSystemImport.
 * @remarks
 * Attempts to use a module federation version of zero before falling back to the version that was bundled with the app.
 * @internal
 */
async function zeroDesignSystemImport(): Promise<DesignSystemModule> {
  let module: DesignSystemModule;
  let type: ResourceType = ResourceType.remote;
  try {
    module = await import(
      /* webpackChunkName: "foundation-zero" */
      'foundationZero/ZeroDesignSystem'
      );
    return assureDesignSystem(module);
  } catch (e) {
    logger.info(
      `Please note remoteEntry.js load errors are expected if module federated dependencies are offline. Falling back to locally bundled versions.`,
    );
    type = ResourceType.local;
    module = await import(
      /* webpackChunkName: "foundation-zero" */
      '@genesislcap/foundation-zero'
      );
    return assureDesignSystem(module);
  } finally {
    logger.debug(`Using '${type}' version of foundation-zero`);
  }
}

/**
 * registerComponents.
 * @public
 */
export async function registerComponents() {
  const designSystem = await zeroDesignSystemImport();
  const {provideDesignSystem, baseComponents} = designSystem;

  /**
   * Register any PBC components with the design system
   */
  getApp().registerComponents({
    designSystem,
  });

  provideDesignSystem().register(
    baseComponents,
    zeroGridComponents,
    g2plotChartsComponents,
    foundationLayoutComponents,
    interopFoundationNotificationListener()
  );
}
