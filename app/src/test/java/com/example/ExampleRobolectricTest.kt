package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.NavigationTransportMode
import com.example.data.navigation.SantipurRoutingEngine
import com.example.data.repository.SantipurRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Santipur Utsabh", appName)
    }

    @Test
    fun `santipur places seed data verification`() {
        val places = SantipurRepository.getAllPlaces()
        assertTrue("Places list should not be empty", places.isNotEmpty())
        assertTrue("Should include Boro Goswami Bari", places.any { it.id == "boro_goswami" })
        assertTrue("Should include Agameswari Mandir", places.any { it.id == "agameswari" })
    }

    @Test
    fun `santipur festivals classification count`() {
        val festivals = SantipurRepository.getAllFestivals()
        assertEquals(14, festivals.size)
    }

    @Test
    fun `in app navigation routing engine test`() {
        val destination = SantipurRepository.getAllPlaces().first { it.id == "boro_goswami" }
        val origin = SantipurRepository.getAllPlaces().first { it.id == "santipur_station" }

        val route = SantipurRoutingEngine.generateRoute(
            destination = destination,
            originPlace = origin,
            mode = NavigationTransportMode.WALKING,
            userCurrentLat = SantipurRepository.SANTIPUR_LAT,
            userCurrentLng = SantipurRepository.SANTIPUR_LNG
        )

        assertNotNull("Generated route should not be null", route)
        assertTrue("Route should contain multiple steps", route.steps.size >= 3)
        assertTrue("Route should have polyline coordinates", route.coordinates.size >= 3)
        assertTrue("Total distance should be greater than 0", route.totalDistanceMeters > 0)
        assertTrue("Estimated minutes should be positive", route.estimatedMinutes > 0)
        assertEquals("boro_goswami", route.destinationPlaceId)
    }
}
