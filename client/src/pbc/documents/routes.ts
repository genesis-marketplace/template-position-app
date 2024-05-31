import type { AppRoute } from '@genesislcap/foundation-shell/app';

/**
 * Document Management route
 * @public
 */
export const document: AppRoute = {
  title: 'Document Management',
  path: 'document-management',
  name: 'document-management',
  element: async () => (await import('@genesislcap/pbc-documents-ui')).FoundationDocumentManager,
  settings: { autoAuth: true, maxRows: 500 },
  navItems: [
    {
      navId: 'header',
      title: 'Document Management',
      icon: {
        name: 'file-csv',
        variant: 'solid',
      },
      placementIndex: 35,
    },
    {
      navId: 'side',
      title: 'Special Document Manager',
      routePath: 'document-management/foo', // < example if there were child routes
      icon: {
        name: 'file-csv',
        variant: 'solid',
      },
    },
  ],
};
