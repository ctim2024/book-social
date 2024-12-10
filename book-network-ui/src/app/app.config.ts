import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { httpTokenInterceptor } from './services/interceptor/http-token.interceptor';
import { ApiModule } from './services/api.module';
import { ApiConfiguration } from './services/api-configuration';


export const appConfig: ApplicationConfig = {

  
  providers: [provideZoneChangeDetection({ eventCoalescing: true }), 
              provideRouter(routes), 
              provideHttpClient(withInterceptors([httpTokenInterceptor])),
                // Provide ApiModule with configuration
    {
      provide: ApiConfiguration,
      useValue: {
        rootUrl: 'http://10.76.100.239:8088/api/v1', // Update this to your API URL
      },
    }]
};
