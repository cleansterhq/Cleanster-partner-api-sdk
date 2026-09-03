package com.cleanster.android

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ChecklistsTest {
    private lateinit var server: MockWebServer
    private lateinit var client: CleansterClient

    @Before fun setUp() {
        server = MockWebServer()
        server.start()
        client = CleansterClient.custom("test-key", server.url("/").toString())
        client.setToken("test-token")
    }

    @After fun tearDown() { server.shutdown() }

    private fun enqueue(body: String, code: Int = 200) =
        server.enqueue(MockResponse().setBody(body).setResponseCode(code)
            .addHeader("Content-Type", "application/json"))

    private val checklistJson =
        """{"id":77,"name":"Deep Clean","items":[{"id":1,"description":"Vacuum"},{"id":2,"description":"Mop","imageUrl":"https://images.example.com/evidence.jpg"},{"id":3,"description":"Wipe surfaces"}]}"""

    @Test fun `listChecklists returns list`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[$checklistJson]}""")
        val resp = client.checklists.listChecklists()
        assertEquals(200, resp.status)
        assertEquals(1, resp.data?.size)
    }

    @Test fun `listChecklists sends GET`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        client.checklists.listChecklists()
        val req = server.takeRequest()
        assertEquals("GET", req.method)
        assert(req.path!!.contains("v1/checklist"))
    }

    @Test fun `getChecklist returns single checklist`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":$checklistJson}""")
        val resp = client.checklists.getChecklist(77)
        assertEquals(77, resp.data?.id)
        assertEquals("Deep Clean", resp.data?.name)
    }

    @Test fun `getChecklist hits correct path`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":$checklistJson}""")
        client.checklists.getChecklist(77)
        val req = server.takeRequest()
        assert(req.path!!.contains("v1/checklist/77"))
    }

    @Test fun `getChecklist returns items`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":$checklistJson}""")
        val resp = client.checklists.getChecklist(77)
        assertEquals(3, resp.data?.items?.size)
        assertEquals("Vacuum", resp.data?.items?.first()?.description)
    }

    @Test fun `getChecklist parses checklist item image URL`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":$checklistJson}""")
        val resp = client.checklists.getChecklist(77)
        assertEquals("https://images.example.com/evidence.jpg", resp.data?.items?.get(1)?.imageUrl)
    }

    @Test fun `createChecklist sends POST`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":$checklistJson}""")
        client.checklists.createChecklist("Deep Clean", listOf("Vacuum", "Mop"))
        val req = server.takeRequest()
        assertEquals("POST", req.method)
        assert(req.path!!.contains("v1/checklist"))
    }

    @Test fun `createChecklist includes name and items in body`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":$checklistJson}""")
        client.checklists.createChecklist("Deep Clean", listOf(
            com.cleanster.android.model.ChecklistTask(
                imageName = "vacuum.jpg",
                title = "Vacuum",
                totalSubtasks = 1,
                subtasks = listOf(com.cleanster.android.model.ChecklistSubtask(
                    description = "Under furniture",
                    flagRequestPhotos = true,
                    photos = listOf("before.jpg"),
                )),
            ),
        ))
        val body = server.takeRequest().body.readUtf8()
        assert(body.contains("\"title\":\"Deep Clean\""))
        assert(body.contains("\"tasks\""))
        assert(body.contains("\"image_name\":\"vacuum.jpg\""))
        assert(body.contains("\"flag_request_photos\":true"))
        assert(!body.contains("\"name\""))
        assert(!body.contains("\"items\""))
    }

    @Test fun `getChecklist parses live nested task fields`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"id":77,"is_default":true,"disabled":false,"title":"Deep Clean","type":"custom","totalTasks":1,"totalSubTasks":1,"tasks":[{"image_name":"vacuum.jpg","title":"Vacuum","totalSubtasks":1,"subtasks":[{"description":"Under furniture","flag_request_photos":true,"photos":["proof.jpg"]}]}]}}""")
        val checklist = client.checklists.getChecklist(77).data!!
        assertEquals("Deep Clean", checklist.title)
        assertEquals("vacuum.jpg", checklist.tasks.single().imageName)
        assertEquals(true, checklist.tasks.single().subtasks.single().flagRequestPhotos)
        assertEquals(listOf("proof.jpg"), checklist.tasks.single().subtasks.single().photos)
    }

    @Test fun `createChecklist returns created checklist`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":$checklistJson}""")
        val resp = client.checklists.createChecklist("Deep Clean", listOf("Vacuum"))
        assertEquals(77, resp.data?.id)
    }

    @Test fun `updateChecklist sends PUT`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":$checklistJson}""")
        client.checklists.updateChecklist(77, "Updated", listOf("Task 1"))
        val req = server.takeRequest()
        assertEquals("PUT", req.method)
        assert(req.path!!.contains("v1/checklist/77"))
    }

    @Test fun `updateChecklist includes new name in body`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":$checklistJson}""")
        client.checklists.updateChecklist(77, "New Name", listOf("T1", "T2"))
        val body = server.takeRequest().body.readUtf8()
        assert(body.contains("New Name"))
        assert(body.contains("T1"))
    }

    @Test fun `deleteChecklist sends DELETE`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.checklists.deleteChecklist(77)
        val req = server.takeRequest()
        assertEquals("DELETE", req.method)
        assert(req.path!!.contains("v1/checklist/77"))
    }

    @Test fun `deleteChecklist returns 200`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        val resp = client.checklists.deleteChecklist(77)
        assertEquals(200, resp.status)
    }

    @Test fun `listChecklists returns empty list`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        val resp = client.checklists.listChecklists()
        assertEquals(0, resp.data?.size)
    }

    @Test fun `createChecklist with many items`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"id":88,"name":"Full","items":[{"description":"i1"},{"description":"i2"},{"description":"i3"},{"description":"i4"},{"description":"i5"}]}}""")
        val resp = client.checklists.createChecklist("Full", listOf("i1","i2","i3","i4","i5"))
        assertEquals(5, resp.data?.items?.size)
    }

    @Test fun `listChecklists includes access-key header`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        client.checklists.listChecklists()
        assertEquals("test-key", server.takeRequest().getHeader("access-key"))
    }

    @Test fun `updateChecklist returns 200`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":$checklistJson}""")
        val resp = client.checklists.updateChecklist(77, "Updated", listOf("T1"))
        assertEquals(200, resp.status)
    }

    @Test fun `getChecklist message is OK`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":$checklistJson}""")
        val resp = client.checklists.getChecklist(77)
        assertEquals("OK", resp.message)
    }

    @Test fun `createChecklist path is v1 checklist`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":$checklistJson}""")
        client.checklists.createChecklist("X", listOf("Y"))
        assert(server.takeRequest().path!!.contains("v1/checklist"))
    }
}
