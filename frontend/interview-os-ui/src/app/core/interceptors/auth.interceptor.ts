import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../../environments/environment';

/**
 * Spring Boot authenticates with an HttpOnly session cookie (JSESSIONID), so
 * every call to interview-os-api must be sent with credentials.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const isApiCall = req.url.startsWith(environment.apiBaseUrl) || req.url.startsWith('/api');
  if (!isApiCall || req.withCredentials) return next(req);
  return next(req.clone({ withCredentials: true }));
};
