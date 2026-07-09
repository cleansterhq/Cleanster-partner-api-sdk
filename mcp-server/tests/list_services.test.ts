import { describe, it, expect, vi, beforeEach } from 'vitest';
import { handler, inputSchema } from '../src/tools/list_services.js';
import type { CleansterApiClient } from '../src/api/cleanster.js';

const MOCK_SERVICES = {
  status: 200,
  data: [
    {
      serviceId: 1,
      name: 'Home',
      publicName: 'House',
      description: 'Homeowners and renters needing a clean home',
      img: 'https://example.com/ic_house.png',
    },
    {
      serviceId: 2,
      name: 'Airbnb',
      publicName: 'Short-Term Rentals',
      description: 'Airbnb and vacation rental turnovers',
      img: 'https://example.com/ic_short_term_rentals.png',
    },
    {
      serviceId: 3,
      name: 'Office',
      publicName: 'Office/Common Area',
      description: 'Offices and shared building spaces',
      img: 'https://example.com/ic_commercial_office_spaces.png',
    },
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
    expect(parsed.data[0]).toEqual({
      serviceId: 1,
      name: 'Home',
      publicName: 'House',
      description: 'Homeowners and renters needing a clean home',
      img: 'https://example.com/ic_house.png',
    });
  });

  it('accepts an empty params object', () => {
    expect(() => inputSchema.parse({})).not.toThrow();
  });
});
