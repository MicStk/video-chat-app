import whisper
import sys
import json
from tempfile import NamedTemporaryFile


# Load the Whisper model:
# model = whisper.load_model('small')


def transcribe(file):
    # For each file, let's store the results in a list of dictionaries.
    model = whisper.load_model('tiny', device='cuda')
    options = dict(language='German')

    transcribe_options = dict(task='transcribe', **options)
    results = []
    filename = (file)

    # Let's get the transcript of the temporary file.
    result = model.transcribe(filename, **transcribe_options)
    # Now we can store the result object for this file.
    for segment in result['segments']:
        results.append({
            'start': segment['start'],
            'end': segment['end'],
            'transcript': segment['text'],
        })

    print(json.dumps(results))


if __name__ == '__main__':
    globals()[sys.argv[1]](sys.argv[2])

# transcribe("audiofiles/test_123_dankeschoen.mp3")
