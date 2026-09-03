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

    @Test fun `getChecklist parses checklist item image URL`() = runTest {
        mock.succeed(mapOf(
            "id" to 77.0,
            "name" to "Deep Clean",
            "items" to listOf(mapOf(
                "id" to 1.0,
                "description" to "Vacuum",
                "isCompleted" to true,
                "imageUrl" to "https://images.example.com/evidence.jpg",
            )),
        ))
        val response = client.checklists.getChecklist(77)
        assertEquals("https://images.example.com/evidence.jpg", response.data?.items?.single()?.imageUrl)
    }

    @Test fun `getChecklist parses live nested task fields`() = runTest {
        mock.succeed(mapOf(
            "id" to 77.0,
            "is_default" to true,
            "disabled" to false,
            "title" to "Deep Clean",
            "type" to "custom",
            "totalTasks" to 1.0,
            "totalSubTasks" to 1.0,
            "tasks" to listOf(mapOf(
                "image_name" to "vacuum.jpg",
                "title" to "Vacuum",
                "totalSubtasks" to 1.0,
                "subtasks" to listOf(mapOf(
                    "description" to "Under furniture",
                    "flag_request_photos" to true,
                    "photos" to listOf("proof.jpg"),
                )),
            )),
        ))
        val checklist = client.checklists.getChecklist(77).data!!
        assertEquals("Deep Clean", checklist.title)
        assertEquals("vacuum.jpg", checklist.tasks?.single()?.imageName)
        assertEquals(true, checklist.tasks?.single()?.subtasks?.single()?.flagRequestPhotos)
        assertEquals(listOf("proof.jpg"), checklist.tasks?.single()?.subtasks?.single()?.photos)
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

    @Test fun `createChecklist encodes only live payload keys`() = runTest {
        mock.succeed(mapOf("id" to 77.0))
        client.checklists.createChecklist("Deep Clean", listOf("Mop"))
        assertEquals(setOf("title", "tasks"), mock.capturedBody?.keys)
        assertEquals("Deep Clean", mock.capturedBody?.get("title"))
    }

    @Test fun `createChecklist encodes structured tasks`() = runTest {
        mock.succeed(mapOf("id" to 77.0))
        client.checklists.createChecklist("Test", listOf("Vacuum", "Mop", "Wipe"))
        @Suppress("UNCHECKED_CAST")
        val tasks = mock.capturedBody?.get("tasks") as? List<*>
        assertEquals(3, tasks?.size)
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

    @Test fun `updateChecklist encodes title`() = runTest {
        mock.succeed(mapOf("id" to 77.0))
        client.checklists.updateChecklist(77, "Renamed", listOf("Task"))
        assertEquals("Renamed", mock.capturedBody?.get("title"))
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
