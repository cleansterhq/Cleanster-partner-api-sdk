package com.cleanster.xml;

import com.cleanster.xml.api.ChecklistsXmlApi;
import com.cleanster.xml.client.XmlConverter;
import com.cleanster.xml.client.XmlHttpClient;
import com.cleanster.xml.model.Checklist;
import com.cleanster.xml.model.XmlApiResponse;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ChecklistsTest {

    private XmlHttpClient   http;
    private ChecklistsXmlApi api;

    private static final String LIST_JSON     = "{\"success\":true,\"message\":\"OK\","
            + "\"data\":[{\"id\":1,\"name\":\"Deep Clean\"}]}";
    private static final String CHECKLIST_JSON = "{\"success\":true,\"message\":\"OK\","
            + "\"data\":{\"id\":1,\"name\":\"Deep Clean\"}}";
    private static final String OK_JSON        = "{\"success\":true,\"message\":\"OK\",\"data\":{}}";

    @BeforeEach void setUp() {
        XmlHttpClient real = XmlHttpClient.builder().baseUrl("http://dummy").accessKey("key").build();
        http = spy(real);
        api  = new ChecklistsXmlApi(http);
    }

    // ── listChecklists ─────────────────────────────────────────────────────────

    @Test void listChecklists_callsGet() {
        doReturn(LIST_JSON).when(http).get("/v1/checklist");
        api.listChecklists();
        verify(http).get("/v1/checklist");
    }

    @Test void listChecklists_returnsList() {
        doReturn(LIST_JSON).when(http).get("/v1/checklist");
        XmlApiResponse<List<Checklist>> resp = api.listChecklists();
        assertTrue(resp.isSuccess());
        assertEquals(1, resp.getData().size());
        assertEquals("Deep Clean", resp.getData().get(0).getName());
    }

    @Test void listChecklists_emptyList() {
        doReturn("{\"success\":true,\"message\":\"OK\",\"data\":[]}").when(http).get("/v1/checklist");
        assertTrue(api.listChecklists().getData().isEmpty());
    }

    // ── getChecklist ───────────────────────────────────────────────────────────

    @Test void getChecklist_callsGetWithId() {
        doReturn(CHECKLIST_JSON).when(http).get("/v1/checklist/1");
        api.getChecklist(1);
        verify(http).get("/v1/checklist/1");
    }

    @Test void getChecklist_differentId_correctPath() {
        doReturn(CHECKLIST_JSON).when(http).get("/v1/checklist/5");
        api.getChecklist(5);
        verify(http).get("/v1/checklist/5");
    }

    @Test void getChecklist_returnsParsed() {
        doReturn(CHECKLIST_JSON).when(http).get("/v1/checklist/1");
        assertEquals("Deep Clean", api.getChecklist(1).getData().getName());
    }

    // ── createChecklist ────────────────────────────────────────────────────────

    @Test void createChecklist_callsPost() {
        doReturn(CHECKLIST_JSON).when(http).post(eq("/v1/checklist"), any());
        api.createChecklist("Deep Clean", List.of("Mop", "Vacuum"));
        verify(http).post(eq("/v1/checklist"), any());
    }

    @Test void createChecklist_returnsParsed() {
        doReturn(CHECKLIST_JSON).when(http).post(eq("/v1/checklist"), any());
        XmlApiResponse<Checklist> resp = api.createChecklist("Deep Clean", List.of());
        assertTrue(resp.isSuccess());
        assertEquals(1, (int) resp.getData().getId());
    }

    // ── updateChecklist ────────────────────────────────────────────────────────

    @Test void updateChecklist_callsPutWithId() {
        doReturn(CHECKLIST_JSON).when(http).put(eq("/v1/checklist/1"), any());
        api.updateChecklist(1, "Updated", List.of("Task1"));
        verify(http).put(eq("/v1/checklist/1"), any());
    }

    // ── deleteChecklist ────────────────────────────────────────────────────────

    @Test void deleteChecklist_callsDeleteWithId() {
        doReturn(OK_JSON).when(http).delete("/v1/checklist/1");
        api.deleteChecklist(1);
        verify(http).delete("/v1/checklist/1");
    }

    @Test void deleteChecklist_returnsSuccess() {
        doReturn(OK_JSON).when(http).delete("/v1/checklist/1");
        assertTrue(api.deleteChecklist(1).isSuccess());
    }

    // ── uploadChecklistImage ───────────────────────────────────────────────────

    @Test void uploadChecklistImage_callsPostMultipart() {
        doReturn(OK_JSON).when(http).postMultipart(eq("/v1/checklist/upload-image"), any(), anyString());
        api.uploadChecklistImage(new byte[]{1, 2, 3}, "photo.jpg");
        verify(http).postMultipart(eq("/v1/checklist/upload-image"), any(), anyString());
    }

    @Test void uploadChecklistImage_returnsSuccess() {
        doReturn(OK_JSON).when(http).postMultipart(eq("/v1/checklist/upload-image"), any(), anyString());
        assertTrue(api.uploadChecklistImage(new byte[]{1}, "photo.jpg").isSuccess());
    }

    // ── JAXB ───────────────────────────────────────────────────────────────────

    @Test void checklist_toXml_isValidXml() {
        Checklist c = new Checklist(); c.setId(1); c.setName("Clean");
        assertTrue(XmlConverter.isXml(XmlConverter.toXml(c)));
    }

    @Test void checklist_fromXml_roundTrip() {
        Checklist o = new Checklist(); o.setId(3); o.setName("Standard");
        Checklist r = XmlConverter.fromXml(XmlConverter.toXml(o), Checklist.class);
        assertEquals(3, (int) r.getId());
        assertEquals("Standard", r.getName());
    }
}
