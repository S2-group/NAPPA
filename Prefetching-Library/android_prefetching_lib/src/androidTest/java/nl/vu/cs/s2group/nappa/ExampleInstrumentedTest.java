package nl.vu.cs.s2group.nappa;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

// TODO Create a test database for the NAPPA library
//  Creating data at runtime takes too much effort, is prone to error and has no representation
//  of a real/simulated case of a NAPPA enabled app.
//  Verify possibility of importing an actual `mydatabase.db` SQLite file.
//  This file can be easily obtained by using the application in a NAPPA enabled app.
//  This method brings the following benefits:
//  * The test data is deterministic;
//  * Little effort in creating the data;
//  * Tests run faster since the data do not need to be created --> valid for a huge amount of data;
//  * It is easier to debug the data;
//  If it is not possible to import a `*.db` file, investigate alternative approaches.
//  Implementing unit and integration tests allows to ensure the quality and correctness of
//  the NAPPA library. It is also easier to test the application: instead of importing the library
//  to the app and manually testing it, these tests allows to run automated tests.
//  The only aspect that is difficult to test with this method is changes in the database
//  schema. For existent test data, the test database could be manually updated, but I would
//  suggest just recreating the data.
/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("nl.vu.cs.s2group.nappa.test", appContext.getPackageName());
    }
}
