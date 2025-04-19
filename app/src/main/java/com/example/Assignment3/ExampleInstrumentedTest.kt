import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.Assignment3.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyUiTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun verifyMindDriveIsSavedAndDisplayed() {

        // Perform UI interactions and assertions
        composeTestRule.onNodeWithText("Events").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText("Comp", substring = true).assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText("MindDrive", substring = true).assertIsDisplayed()
            .performTouchInput { longClick() }
        composeTestRule.onNodeWithText("Itin").performClick()
        composeTestRule.onNodeWithText("MindDrive", substring = true).assertIsDisplayed()
    }


    @Test
    fun verifyBackButton(): Unit {
        composeTestRule.onNodeWithText("Events").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText("Comp", substring = true).assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText("MindDrive", substring = true).assertIsDisplayed()

        // Simulate pressing the back button
        Espresso.pressBack()

        composeTestRule.onNodeWithText("Comm", substring = true).assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText("Talk", substring = true).assertIsDisplayed()
    }

    @Test
    fun verifyTelCount() {
        composeTestRule.onNodeWithTag("Info").assertIsDisplayed().performClick()

        // Count the number of nodes with text "3411"
        composeTestRule.onAllNodesWithText("3411", substring = true).assertCountEquals(3)
    }
}

