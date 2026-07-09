import { describe, it, expect, vi, beforeEach } from 'vitest';
import { handler, inputSchema } from '../src/tools/list_services.js';
import type { CleansterApiClient } from '../src/api/cleanster.js';

const MOCK_SERVICES = {
  status: 200,
  data: [
    { id: 1, name: 'Residential' },
    { id: 2, name: 'Airbnb' },
    { id: 3, name: 'Office' },
  ],
};

describe('list_services tool', () => {
  let mockApi: Partial<CleansterApiClient>;

  beforeEach(() => {
    mockApi = {
      getServices: vi.fn().mockResolvedValue(MOCK_SERVICES),
    };
  });

  it('calls api.getServices with no arguments', async () => {
    const params = inputSchema.parse({});
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.getServices).toHaveBeenCalledWith();
  });

  it('returns the service list in content', async () => {
    const params = inputSchema.parse({});
    const result = await handler(params, mockApi as CleansterApiClient);
    const parsed = JSON.parse(result.content[0].text);
    expect(parsed.data).toHaveLength(3);
    expect(parsed.data[0]).toEqual({ id: 1, name: 'Residential' });
  });

  it('accepts an empty params object', () => {
    expect(() => inputSchema.parse({})).not.toThrow();
  });
});
