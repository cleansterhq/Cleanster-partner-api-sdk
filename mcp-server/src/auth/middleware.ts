/**
 * Express middleware for bearer-token authentication on HTTP/SSE routes.
 * Attaches the validated TokenInfo to req.tokenInfo for downstream use.
 */

import type { Request, Response, NextFunction } from 'express';
import { validateBearerToken, type TokenInfo } from './token.js';

declare global {
  namespace Express {
    interface Request {
      tokenInfo?: TokenInfo;
    }
  }
}

export function requireAuth(req: Request, res: Response, next: NextFunction): void {
  const tokenInfo = validateBearerToken(req.headers['authorization']);
  if (!tokenInfo) {
    res.status(401).json({
      error: 'Unauthorized',
      message: 'Provide a valid Bearer token in the Authorization header.',
    });
    return;
  }
  req.tokenInfo = tokenInfo;
  next();
}
