import { describe, it, expect, vi } from 'vitest';
import { validateBearerToken, hasScope } from '../src/auth/token.js';
import { requireAuth } from '../src/auth/middleware.js';
import type { Request, Response, NextFunction } from 'express';

// ── validateBearerToken ────────────────────────────────────────────────────────

describe('validateBearerToken', () => {
  it('extracts token from a valid Authorization header', () => {
    const result = validateBearerToken('Bearer sk_test_abc123');
    expect(result).not.toBeNull();
    expect(result!.token).toBe('sk_test_abc123');
  });

  it('is case-insensitive for the Bearer prefix', () => {
    const result = validateBearerToken('bearer my-token');
    expect(result).not.toBeNull();
    expect(result!.token).toBe('my-token');
  });

  it('returns null for undefined header', () => {
    expect(validateBearerToken(undefined)).toBeNull();
  });

  it('returns null for empty string', () => {
    expect(validateBearerToken('')).toBeNull();
  });

  it('returns null when Bearer keyword is missing', () => {
    expect(validateBearerToken('sk_test_abc123')).toBeNull();
  });

  it('returns null when token is only whitespace after Bearer', () => {
    expect(validateBearerToken('Bearer   ')).toBeNull();
  });

  it('returns null for Basic auth scheme', () => {
    expect(validateBearerToken('Basic dXNlcjpwYXNz')).toBeNull();
  });

  it('grants all expected scopes to every valid token', () => {
    const result = validateBearerToken('Bearer any-token');
    expect(result!.scopes).toEqual(
      expect.arrayContaining([
        'bookings:read',
        'bookings:write',
        'properties:read',
        'cleaners:read',
        'payouts:read',
        'checklists:write',
      ]),
    );
  });

  it('preserves tokens with special characters', () => {
    const token = 'eye.JhbGciO.iJSUzI1NiJ9==';
    const result = validateBearerToken(`Bearer ${token}`);
    expect(result!.token).toBe(token);
  });
});

// ── hasScope ──────────────────────────────────────────────────────────────────

describe('hasScope', () => {
  const tokenInfo = {
    token: 'sk_test',
    scopes: ['bookings:read', 'properties:read'],
  };

  it('returns true when the scope is present', () => {
    expect(hasScope(tokenInfo, 'bookings:read')).toBe(true);
  });

  it('returns false when the scope is absent', () => {
    expect(hasScope(tokenInfo, 'bookings:write')).toBe(false);
  });

  it('is exact-match (no prefix matching)', () => {
    expect(hasScope(tokenInfo, 'bookings')).toBe(false);
    expect(hasScope(tokenInfo, 'read')).toBe(false);
  });
});

// ── requireAuth middleware ─────────────────────────────────────────────────────

function makeReq(authHeader?: string): Request {
  return {
    headers: { authorization: authHeader },
  } as unknown as Request;
}

function makeRes() {
  const res = {
    status: vi.fn().mockReturnThis(),
    json: vi.fn().mockReturnThis(),
  };
  return res as unknown as Response;
}

describe('requireAuth middleware', () => {
  it('calls next() and attaches tokenInfo for a valid Bearer token', () => {
    const req = makeReq('Bearer sk_live_test');
    const res = makeRes();
    const next: NextFunction = vi.fn();

    requireAuth(req, res, next);

    expect(next).toHaveBeenCalledOnce();
    expect((req as Request & { tokenInfo: unknown }).tokenInfo).toMatchObject({
      token: 'sk_live_test',
    });
  });

  it('returns 401 and does not call next() when Authorization header is missing', () => {
    const req = makeReq(undefined);
    const res = makeRes();
    const next: NextFunction = vi.fn();

    requireAuth(req, res, next);

    expect(next).not.toHaveBeenCalled();
    expect(res.status).toHaveBeenCalledWith(401);
    expect(res.json).toHaveBeenCalledWith(
      expect.objectContaining({ error: 'Unauthorized' }),
    );
  });

  it('returns 401 for a malformed Authorization header', () => {
    const req = makeReq('NotBearer abc');
    const res = makeRes();
    const next: NextFunction = vi.fn();

    requireAuth(req, res, next);

    expect(next).not.toHaveBeenCalled();
    expect(res.status).toHaveBeenCalledWith(401);
  });

  it('returns 401 when token is only whitespace', () => {
    const req = makeReq('Bearer    ');
    const res = makeRes();
    const next: NextFunction = vi.fn();

    requireAuth(req, res, next);

    expect(next).not.toHaveBeenCalled();
    expect(res.status).toHaveBeenCalledWith(401);
  });
});
