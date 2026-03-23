import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest {

    @Test
    public void testAppExists() {
        assertTrue(true);
    }

    @Test
    public void testTrainCreation() {
        OnlineReservationSystemGUI.Train t =
            new OnlineReservationSystemGUI.Train(1, "Test", "A", "B", 100);

        assertEquals(100, t.availableSeats);
    }
}