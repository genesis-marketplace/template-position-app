import { defaultLoginConfig, LoginConfig, Settings as LoginSettings } from '@genesislcap/foundation-login';
import { FoundationRouterConfiguration } from '@genesislcap/foundation-ui';
import { defaultLayout, loginLayout } from '../layouts';
import { Constructable } from '@microsoft/fast-element';
import { Container, optional } from '@microsoft/fast-foundation';
import { Route, RouterConfiguration } from '@microsoft/fast-router';
import {
  Auth,
  FoundationAnalytics,
  FoundationAnalyticsEvent,
  FoundationAnalyticsEventType,
  Session,
} from '@genesislcap/foundation-comms';
import { Home } from './home/home';
import { NotFound } from './not-found/not-found';
import { Audit } from './audit/audit';
import { Admin } from './admin/admin';
import { RefData } from './refdata/refdata';


// eslint-disable-next-line
declare var ENABLE_SSO: string;

const ssoSettings = typeof ENABLE_SSO !== 'undefined' && ENABLE_SSO === 'true'
  ? {
      autoAuth: true,
      sso: {
        toggled: true,
        identityProvidersPath: 'gwf/sso/list',
      },
    }
  : {};

export class MainRouterConfig extends FoundationRouterConfiguration<LoginSettings> {
  constructor(
    @Auth private auth: Auth,
    @Session private session: Session,
    @optional(LoginConfig)
    private loginConfig: LoginConfig = { ...defaultLoginConfig, autoAuth: true, autoConnect: true }
  ) {
    super();
  }

  async configure() {
    this.title = 'Positions';
    this.defaultLayout = defaultLayout;

    const authPath = 'login'

    this.routes.map(
      { path: '', redirect: authPath },
      {
        path: authPath,
        element: async () => {
          const { Login, configure } = await import('@genesislcap/foundation-login');
          configure(this.container, {
            autoConnect: true,
            defaultRedirectUrl: 'home',
            ...ssoSettings,
          });
          return Login;
        },
        title: 'Login',
        name: 'login',
        settings: { public: true },
        childRouters: true,
        layout: loginLayout,
      },
      { path: 'home', element: Home, title: 'Home', name: 'home', navItems: [{ title: 'Home', icon: { name: 'home', variant: 'solid',},},],},
      { path: 'not-found', element: NotFound, title: 'Not Found', name: 'not-found' },
      { path: 'audit', element: Audit, title: 'Audit', name: 'audit', navItems: [{ title: 'Audit', icon: { name: 'cog', variant: 'solid',},},],},
      { path: 'refdata', element: RefData, title: 'RefData', name: 'refData', navItems: [{ title: 'Ref Data', icon: { name: 'cog', variant: 'solid',},},],},
      { path: 'admin', element: Admin, title: 'Admin', name: 'admin', navItems: [{ title: 'Admin', icon: { name: 'user', variant: 'solid',},},],},
    );

    /**
     * Example of a FallbackRouteDefinition
     */
    this.routes.fallback(() =>
      this.auth.isLoggedIn ? { redirect: 'not-found' } : { redirect: authPath }
    );

    /**
     * Example of a NavigationContributor
     */
    this.contributors.push({
      navigate: async (phase) => {
        const settings = phase.route.settings;

        /**
         * If public route don't block
         */
        if (settings && settings.public) {
          return;
        }

        /**
         * If logged in don't block
         */
        if (this.auth.isLoggedIn) {
          return;
        }

        /**
         * If allowAutoAuth and session is valid try to connect+auto-login
         */
        if (this.loginConfig.autoAuth && (await this.auth.reAuthFromSession())) {
          return;
        }

        /**
         * Otherwise route them somewhere, like to a login
         */
        phase.cancel(() => {
          this.session.captureReturnUrl();
          Route.name.replace(phase.router, authPath);
        });
      },
    });
  }
}
