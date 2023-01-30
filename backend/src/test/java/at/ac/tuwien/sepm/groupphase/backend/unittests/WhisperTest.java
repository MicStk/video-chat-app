
package at.ac.tuwien.sepm.groupphase.backend.unittests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class WhisperTest {

    //@Test
    void transcriptAudioFile() throws IOException {
        Process p = Runtime.getRuntime().exec("python python/whisperAI.py transcribe audiofiles/test_123_dankeschoen.mp3");

        BufferedReader stdInput = new BufferedReader(new
            InputStreamReader(p.getInputStream()));

        String s = null;
        while ((s = stdInput.readLine()) != null) {
            assertThat(s.contains("[{'start': 0.0, 'end': 5.0, 'transcript': ' Test 1, 2, 3,"));
        }

    }
}


