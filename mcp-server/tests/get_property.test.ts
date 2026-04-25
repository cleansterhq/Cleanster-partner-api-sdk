import { describe, it, expect, vi, beforeEach } from 'vitest';
import { handler, inputSchema } from '../src/tools/get_property.js';
import type { CleansterApiClient } from '../src/api/cleanster.js';

const MOCK_PROPERTY = {
  id: 'prop_42',
  address: '123 Main St',
  city: 'Atlanta',
  state: 'GA',
  zip: '30301',
  type: 'apartment',
  bedrooms: 2,
  bathrooms: 1,
  access_instructions: 'Key in lockbox, code 1234',
  cleaners: [{ id: 'cl_789', name: 'Maria S.' }],
  ical_url: 'https://airbnb.com/calendar/abc.ics',
};

describe('get_property tool', () => {
  let mockApi: Partial<CleansterApiClient>;

  beforeEach(() => {
    mockApi = {
      getProperty: vi.fn().mockResolvedValue({ status: 200, data: MOCK_PROPERTY }),
    };
  });

  it('calls api.getProperty with the correct property ID', async () => {
    const params = inputSchema.parse({ property_id: 'prop_42' });
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.getProperty).toHaveBeenCalledWith('prop_42');
  });

  it('returns full property details in content', async () => {
    const params = inputSchema.parse({ property_id: 'prop_42' });
    const result = await handler(params, mockApi as CleansterApiClient);
    const parsed = JSON.parse(result.content[0].text);
    expect(parsed.data.access_instructions).toBe('Key in lockbox, code 1234');
    expect(parsed.data.cleaners).toHaveLength(1);
  });

  it('requires property_id', () => {
    expect(() => inputSchema.parse({})).toThrow();
  });
});
