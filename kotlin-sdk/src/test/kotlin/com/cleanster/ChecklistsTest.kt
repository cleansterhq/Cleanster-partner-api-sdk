package com.cleanster

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChecklistsTest {

    private lateinit var mock: MockHttpEngine
    private lateinit var client: CleansterClient

    @BeforeEach fun setUp() {
        mock   = MockHttpEngine()
        client = testClient(mock)
    }

    @Test fun `listChecklists sends GET`() = runTest {
        mock.succeedList(); client.checklists.listChecklists()
        assertEquals("GET", mock.capturedMethod)
    }

    @Test fun `listChecklists correct path`() = runTest {
        mock.succeedList(); client.checklists.listChecklists()
        assertTrue(mock.capturedUrl?.endsWith("/v1/checklist") == true)
    }

    @Test fun `getChecklist sends GET`() = runTest {
        mock.succeed(mapOf("id" to 77.0, "name" to "Deep Clean"))
        client.checklists.getChecklist(77)
        assertEquals("GET", mock.capturedMethod)
    }

    @Test fun `getChecklist interpolates id`() = runTest {
        mock.succeed(mapOf("id" to 77.0, "name" to "Deep Clean"))
        client.checklists.getChecklist(77)
        assertTrue(mock.capturedUrl?.endsWith("/v1/checklist/77") == true)
    }

    @Test fun `createChecklist sends POST`() = runTest {
        mock.succeed(mapOf("id" to 77.0))
        client.checklists.createChecklist("Standard", listOf("Vacuum"))
        assertEquals("POST", mock.capturedMethod)
    }

    @Test fun `createChecklist correct path`() = runTest {
        mock.succeed(mapOf("id" to 77.0))
        client.checklists.createChecklist("Standard", listOf("Vacuum"))
        assertTrue(mock.capturedUrl?.endsWith("/v1/checklist") == true)
    }

    @Test fun `createChecklist encodes name`() = runTest {
        mock.succeed(mapOf("id" to 77.0))
        client.checklists.createChecklist("Deep Clean", listOf("Mop"))
        assertEquals("Deep Clean", mock.capturedBody?.get("name"))
    }

    @Test fun `createChecklist encodes items`() = runTest {
        mock.succeed(mapOf("id" to 77.0))
        client.checklists.createChecklist("Test", listOf("Vacuum", "Mop", "Wipe"))
        @Suppress("UNCHECKED_CAST")
        val items = mock.capturedBody?.get("items") as? List<String>
        assertEquals(listOf("Vacuum", "Mop", "Wipe"), items)
    }

    @Test fun `createChecklist decodes id`() = runTest {
        mock.succeed(mapOf("id" to 42.0, "name" to "My List"))
        val resp = client.checklists.createChecklist("My List", listOf("Task 1"))
        assertEquals(42, resp.data?.id)
    }

    @Test fun `updateChecklist sends PUT`() = runTest {
        mock.succeed(mapOf("id" to 77.0))
        client.checklists.updateChecklist(77, "Updated", listOf("New task"))
        assertEquals("PUT", mock.capturedMethod)
    }

    @Test fun `updateChecklist correct path`() = runTest {
        mock.succeed(mapOf("id" to 77.0))
        client.checklists.updateChecklist(77, "Updated", listOf("Task"))
        assertTrue(mock.capturedUrl?.endsWith("/v1/checklist/77") == true)
    }

    @Test fun `updateChecklist encodes name`() = runTest {
        mock.succeed(mapOf("id" to 77.0))
        client.checklists.updateChecklist(77, "Renamed", listOf("Task"))
        assertEquals("Renamed", mock.capturedBody?.get("name"))
    }

    @Test fun `deleteChecklist sends DELETE`() = runTest {
        mock.succeedEmpty(); client.checklists.deleteChecklist(77)
        assertEquals("DELETE", mock.capturedMethod)
    }

    @Test fun `deleteChecklist correct path`() = runTest {
        mock.succeedEmpty(); client.checklists.deleteChecklist(77)
        assertTrue(mock.capturedUrl?.endsWith("/v1/checklist/77") == true)
    }

    @Test fun `uploadChecklistImage uses correct path`() = runTest {
        mock.succeedEmpty()
        client.checklists.uploadChecklistImage(byteArrayOf(0xFF.toByte(), 0xD8.toByte()), "photo.jpg")
        assertTrue(mock.capturedMultipartUrl?.endsWith("/v1/checklist/upload-image") == true)
    }

    @Test fun `uploadChecklistImage passes correct fileName`() = runTest {
        mock.succeedEmpty()
        client.checklists.uploadChecklistImage(byteArrayOf(0x89.toByte(), 0x50.toByte()), "image.png")
        assertEquals("image.png", mock.capturedMultipartFile)
    }
}
