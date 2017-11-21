package main.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jflac.PCMProcessor;
import org.jflac.FLACDecoder;
import org.jflac.metadata.StreamInfo;
import org.jflac.util.ByteData;
import org.jflac.util.WavWriter;

import edu.cmu.cs.cs214.hw5.framework.core.DataLoader;

import javax.swing.*;


/**
 * Converts a FLAC to mp3 and loads it into the framework
 */
public class FLACLoader implements DataLoader {

    @Override
    public File onSelect() {
        boolean isFlac;
        File source;
        do {
            JFileChooser chooser = new JFileChooser(); //JAVA Swing file Chooser
            chooser.showOpenDialog(null);
            source = chooser.getSelectedFile();
            String name = source.getName();
            int ext = name.lastIndexOf('.');
            String extension = name.substring(ext + 1);
            isFlac=extension.equals("flac");
            if(!isFlac) System.out.println("Incorrect file format! Try Again.");
        }while(!isFlac);
        String target="FLAC-song.wav";
        try {
            Decoder decoder = new Decoder();
            decoder.decode(source.getPath(), target);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (new File(target));
    }

    /**
     *  to represent the name of the class
     * @return - the actual string value
     */
    @Override
    public String toString() {
        return "FLAC Loader";
    }

    /**
     * Code takes form the sample of the library. It converts a flac to a wav
     */
    class Decoder implements PCMProcessor {
        private WavWriter wav;

        /**
         * Decode a FLAC file to a WAV file.
         *
         * @param inFileName  The input FLAC file name
         * @param outFileName The output WAV file name
         * @throws IOException Thrown if error reading or writing files
         */
        public void decode(String inFileName, String outFileName) throws IOException {
            System.out.println("Decode [" + inFileName + "][" + outFileName + "]");
            FileInputStream is = null;
            FileOutputStream os = null;
            try {
                is = new FileInputStream(inFileName);
                os = new FileOutputStream(outFileName);
                wav = new WavWriter(os);
                FLACDecoder decoder = new FLACDecoder(is);
                decoder.addPCMProcessor(this);
                decoder.decode();
            } finally {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            }
        }

        /**
         * Process the StreamInfo block.
         *
         * @param info the StreamInfo block
         * @see org.jflac.PCMProcessor#processStreamInfo(org.jflac.metadata.StreamInfo)
         */
        public void processStreamInfo(StreamInfo info) {
            try {
                wav.writeHeader(info);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Process the decoded PCM bytes.
         *
         * @param pcm The decoded PCM data
         * @see org.jflac.PCMProcessor
         */
        public void processPCM(ByteData pcm) {
            try {
                wav.writePCM(pcm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
