import java.io.File;
import javax.sound.sampled.*;

class SoundPlay implements Runnable {
	private String filename;
	public SoundPlay(String filename) {
		this.filename = filename;
	}

	public void run() {
		File sound_file = new File(filename);
		AudioInputStream audio_input_stream = null;
		try { 
			audio_input_stream = AudioSystem.getAudioInputStream(sound_file);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		AudioFormat format = audio_input_stream.getFormat();
		SourceDataLine audio_line = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

		try {
			audio_line = (SourceDataLine) AudioSystem.getLine(info);
			audio_line.open(format);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		audio_line.start();
		int n_byte_read = 0;
		byte[] data_buffer = new byte[1024];
		try {
			while (n_byte_read!=-1) {
				n_byte_read = audio_input_stream.read(data_buffer, 0, data_buffer.length);
				if(n_byte_read>=0)
					audio_line.write(data_buffer, 0, n_byte_read);
				}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			audio_line.drain();
			audio_line.close();
		}
	}
}