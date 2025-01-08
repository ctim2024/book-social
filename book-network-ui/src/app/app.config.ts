import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { ApiConfiguration } from './services/api-configuration';
import { httpTokenInterceptor } from './services/interceptor/http-token.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [provideZoneChangeDetection({ eventCoalescing: true }), 
              provideRouter(routes), 
              provideHttpClient(withInterceptors([httpTokenInterceptor])),
                            // Provide ApiModule with configuration
    {
      provide: ApiConfiguration,
      useValue: {
        rootUrl: 'http://10.71.1.53:8088/api/v1', // Update this to your API URL
      }
    }
    ]
};
