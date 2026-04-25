import { describe, it, expect, vi, beforeEach } from 'vitest';
import { handler, inputSchema } from '../src/tools/list_properties.js';
import type { CleansterApiClient } from '../src/api/cleanster.js';

const MOCK_PROPERTIES = [
  {
    id: 'prop_42',
    address: '123 Main St',
    city: 'Atlanta',
    state: 'GA',
    zip: '30301',
    type: 'apartment',
    bedrooms: 2,
    bathrooms: 1,
  },
  {
    id: 'prop_99',
    address: '456 Oak Ave',
    city: 'Savannah',
    state: 'GA',
    zip: '31401',
    type: 'house',
    bedrooms: 4,
    bathrooms: 3,
  },
];

describe('list_properties tool', () => {
  let mockApi: Partial<CleansterApiClient>;

  beforeEach(() => {
    mockApi = {
      listProperties: vi.fn().mockResolvedValue({ status: 200, data: MOCK_PROPERTIES }),
    };
  });

  it('calls api.listProperties with no filters when none provided', async () => {
    const params = inputSchema.parse({});
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.listProperties).toHaveBeenCalledWith({});
  });

  it('passes property_type filter to api', async () => {
    const params = inputSchema.parse({ property_type: 'apartment' });
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.listProperties).toHaveBeenCalledWith({ property_type: 'apartment' });
  });

  it('returns serialised property list', async () => {
    const params = inputSchema.parse({});
    const result = await handler(params, mockApi as CleansterApiClient);
    const parsed = JSON.parse(result.content[0].text);
    expect(parsed.data).toHaveLength(2);
    expect(parsed.data[1].city).toBe('Savannah');
  });

  it('rejects invalid property_type', () => {
    expect(() => inputSchema.parse({ property_type: 'villa' })).toThrow();
  });

  it('accepts all valid property_type values', () => {
    const valid = ['apartment', 'house', 'str', 'commercial'];
    valid.forEach((t) => expect(() => inputSchema.parse({ property_type: t })).not.toThrow());
  });
});
