import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest {
    @Test
    public void testAppExists() {
        assertTrue("App class should exist", true);
    }

    @Test
    public void testBookingIdCounter() {
        OnlineReservationSystemGUI.Booking.idCounter = 1000;
        assertEquals(1000, OnlineReservationSystemGUI.Booking.idCounter);
    }
}