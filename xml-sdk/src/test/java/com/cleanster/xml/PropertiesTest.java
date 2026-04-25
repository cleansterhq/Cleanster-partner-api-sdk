package com.cleanster.xml;

import com.cleanster.xml.api.PropertiesXmlApi;
import com.cleanster.xml.client.XmlConverter;
import com.cleanster.xml.client.XmlHttpClient;
import com.cleanster.xml.model.Property;
import com.cleanster.xml.model.XmlApiResponse;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PropertiesTest {

    private XmlHttpClient   http;
    private PropertiesXmlApi api;

    private static final String LIST_JSON = "{\"success\":true,\"message\":\"OK\","
            + "\"data\":[{\"id\":1,\"name\":\"Beach House\"}]}";
    private static final String PROP_JSON = "{\"success\":true,\"message\":\"OK\","
            + "\"data\":{\"id\":1,\"name\":\"Beach House\"}}";
    private static final String OK_JSON   = "{\"success\":true,\"message\":\"OK\",\"data\":{}}";

    @BeforeEach void setUp() {
        XmlHttpClient real = XmlHttpClient.builder().baseUrl("http://dummy").accessKey("key").build();
        http = spy(real);
        api  = new PropertiesXmlApi(http);
    }

    // ── listProperties ─────────────────────────────────────────────────────────

    @Test void listProperties_noFilter_callsGet() {
        doReturn(LIST_JSON).when(http).get("/v1/properties");
        api.listProperties();
        verify(http).get("/v1/properties");
    }

    @Test void listProperties_withServiceId_callsGetWithParam() {
        doReturn(LIST_JSON).when(http).get("/v1/properties?serviceId=2");
        api.listProperties(2);
        verify(http).get("/v1/properties?serviceId=2");
    }

    @Test void listProperties_returnsList() {
        doReturn(LIST_JSON).when(http).get("/v1/properties");
        XmlApiResponse<List<Property>> resp = api.listProperties();
        assertTrue(resp.isSuccess());
        assertEquals(1, resp.getData().size());
    }

    // ── createProperty ─────────────────────────────────────────────────────────

    @Test void createProperty_callsPost() {
        doReturn(PROP_JSON).when(http).post(eq("/v1/properties"), any());
        api.createProperty("Beach House", "123 Main St", "Miami", "US", 3, 2, 1);
        verify(http).post(eq("/v1/properties"), any());
    }

    @Test void createProperty_returnsParsed() {
        doReturn(PROP_JSON).when(http).post(eq("/v1/properties"), any());
        XmlApiResponse<Property> resp = api.createProperty("Beach House", "123", "Miami", "US", 3, 2, 1);
        assertTrue(resp.isSuccess());
        assertEquals(1, (int) resp.getData().getId());
    }

    @Test void createProperty_mapOverload_callsPost() {
        doReturn(PROP_JSON).when(http).post(eq("/v1/properties"), any());
        api.createProperty(Map.of("name", "Beach House"));
        verify(http).post(eq("/v1/properties"), any());
    }

    // ── getProperty ────────────────────────────────────────────────────────────

    @Test void getProperty_callsGetWithId() {
        doReturn(PROP_JSON).when(http).get("/v1/properties/1");
        api.getProperty(1);
        verify(http).get("/v1/properties/1");
    }

    @Test void getProperty_returnsParsed() {
        doReturn(PROP_JSON).when(http).get("/v1/properties/1");
        assertEquals("Beach House", api.getProperty(1).getData().getName());
    }

    // ── updateProperty ─────────────────────────────────────────────────────────

    @Test void updateProperty_callsPutWithId() {
        doReturn(PROP_JSON).when(http).put(eq("/v1/properties/1"), any());
        api.updateProperty(1, Map.of("name", "Updated House"));
        verify(http).put(eq("/v1/properties/1"), any());
    }

    // ── deleteProperty ─────────────────────────────────────────────────────────

    @Test void deleteProperty_callsDeleteWithId() {
        doReturn(OK_JSON).when(http).delete("/v1/properties/1");
        api.deleteProperty(1);
        verify(http).delete("/v1/properties/1");
    }

    @Test void deleteProperty_returnsSuccess() {
        doReturn(OK_JSON).when(http).delete("/v1/properties/1");
        assertTrue(api.deleteProperty(1).isSuccess());
    }

    // ── updateAdditionalInformation ────────────────────────────────────────────

    @Test void updateAdditionalInformation_callsPutWithPath() {
        doReturn(OK_JSON).when(http).put(eq("/v1/properties/1/additional-information"), any());
        api.updateAdditionalInformation(1, Map.of("notes", "Pets allowed"));
        verify(http).put(eq("/v1/properties/1/additional-information"), any());
    }

    // ── enableOrDisableProperty ────────────────────────────────────────────────

    @Test void enableOrDisableProperty_callsPostWithPath() {
        doReturn(OK_JSON).when(http).post(eq("/v1/properties/1/enable-disable"), any());
        api.enableOrDisableProperty(1, true);
        verify(http).post(eq("/v1/properties/1/enable-disable"), any());
    }

    // ── cleaner assignment ─────────────────────────────────────────────────────

    @Test void getPropertyCleaners_callsGetWithPath() {
        doReturn(OK_JSON).when(http).get("/v1/properties/1/cleaners");
        api.getPropertyCleaners(1);
        verify(http).get("/v1/properties/1/cleaners");
    }

    @Test void assignCleanerToProperty_callsPostWithPath() {
        doReturn(OK_JSON).when(http).post(eq("/v1/properties/1/cleaners"), any());
        api.assignCleanerToProperty(1, 42);
        verify(http).post(eq("/v1/properties/1/cleaners"), any());
    }

    @Test void unassignCleanerFromProperty_callsDeleteWithPath() {
        doReturn(OK_JSON).when(http).delete("/v1/properties/1/cleaners/42");
        api.unassignCleanerFromProperty(1, 42);
        verify(http).delete("/v1/properties/1/cleaners/42");
    }

    // ── iCal ───────────────────────────────────────────────────────────────────

    @Test void addICalLink_callsPutWithPath() {
        doReturn(OK_JSON).when(http).put(eq("/v1/properties/1/ical"), any());
        api.addICalLink(1, "https://cal.example.com/ical");
        verify(http).put(eq("/v1/properties/1/ical"), any());
    }

    @Test void getICalLink_callsGetWithPath() {
        doReturn(OK_JSON).when(http).get("/v1/properties/1/ical");
        api.getICalLink(1);
        verify(http).get("/v1/properties/1/ical");
    }

    @Test void removeICalLink_callsDeleteWithPath() {
        doReturn(OK_JSON).when(http).delete("/v1/properties/1/ical");
        api.removeICalLink(1, null);
        verify(http).delete("/v1/properties/1/ical");
    }

    // ── setDefaultChecklist ────────────────────────────────────────────────────

    @Test void setDefaultChecklist_callsPutWithPath() {
        String path = "/v1/properties/1/checklist/5?updateUpcomingBookings=true";
        doReturn(OK_JSON).when(http).put(eq(path), any());
        api.setDefaultChecklist(1, 5, true);
        verify(http).put(eq(path), any());
    }

    // ── JAXB ───────────────────────────────────────────────────────────────────

    @Test void property_toXml_isValidXml() {
        Property p = new Property(); p.setId(1); p.setName("Test");
        assertTrue(XmlConverter.isXml(XmlConverter.toXml(p)));
    }

    @Test void property_fromXml_roundTrip() {
        Property o = new Property(); o.setId(3); o.setName("Beach");
        Property r = XmlConverter.fromXml(XmlConverter.toXml(o), Property.class);
        assertEquals(3, (int) r.getId());
        assertEquals("Beach", r.getName());
    }
}
