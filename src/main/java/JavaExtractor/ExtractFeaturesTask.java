package JavaExtractor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;

import com.github.javaparser.ParseException;

import JavaExtractor.Common.CommandLineValues;
import JavaExtractor.Common.Common;
import JavaExtractor.FeaturesEntities.ProgramFeatures;

public class ExtractFeaturesTask implements Callable<Void> {
    CommandLineValues m_CommandLineValues;
    Path filePath;

    public ExtractFeaturesTask(CommandLineValues commandLineValues, Path path) {
        m_CommandLineValues = commandLineValues;
        this.filePath = path;
    }

    @Override
    public Void call() throws Exception {
        //System.err.println("Extracting file: " + filePath);
        processFile();
        //System.err.println("Done with file: " + filePath);
        return null;
    }

    public void processFile() {
        String fn = filePath.toFile().getName() + " ";
        ArrayList<ProgramFeatures> features;
        try {
            features = extractSingleFile();
        } catch (Exception e) {
            String str1 = e.getClass().getName() + " " + e.getMessage();
            String str2 = str1.replace("\n", "\\n");
            System.out.println(fn + "FAILED\t" + str2);
            e.printStackTrace();
            return;
        }
        if (features == null) {
            System.out.println(fn + "FAILED\tNULL");
            return;
        }

        String toPrint = featuresToString(features, m_CommandLineValues);
        if (toPrint.length() > 0) {
            System.out.println(fn + "OK\t" + toPrint);
//            System.out.println(toPrint);
        } else
            System.out.println(fn + "FAILED\t toPrint.length()==0");
    }

    public ArrayList<ProgramFeatures> extractSingleFile() throws ParseException, IOException {
        String code = null;
        try {
            code = new String(Files.readAllBytes(this.filePath));
        } catch (IOException e) {
            e.printStackTrace();
            code = Common.EmptyString;
        }
        FeatureExtractor featureExtractor = new FeatureExtractor(m_CommandLineValues);

        ArrayList<ProgramFeatures> features = featureExtractor.extractFeatures(code);

        return features;
    }

    public static String featuresToString(ArrayList<ProgramFeatures> features, CommandLineValues m_commandLineValues) {
        if (features == null || features.isEmpty()) {
            return Common.EmptyString;
        }

        List<String> methodsOutputs = new ArrayList<>();

        for (ProgramFeatures singleMethodfeatures : features) {
            StringBuilder builder = new StringBuilder();

            String toPrint = Common.EmptyString;
            toPrint = singleMethodfeatures.toString();
            if (m_commandLineValues.PrettyPrint) {
                toPrint = toPrint.replace(" ", "\n\t");
            }
            builder.append(toPrint);


            methodsOutputs.add(builder.toString());

        }
        return StringUtils.join(methodsOutputs, "\n");
    }
}
