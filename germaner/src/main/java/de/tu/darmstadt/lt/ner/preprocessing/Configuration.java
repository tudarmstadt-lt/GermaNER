package de.tu.darmstadt.lt.ner.preprocessing;

public class Configuration
{
    public static String mode;
    public static String modelDir;    
    public static String testFileName;
    public static String trainFileName;
    
    // Features
    public static boolean usePosition;
    public static boolean useFreeBase;
    public static boolean useClarkPosInduction;
    public static boolean useWordFeature;
    public static boolean useCapitalFeature;
    public static boolean usePreffix1Feature;
    public static boolean usePreffix2Feature;
    public static boolean usePreffix3Feature;
    public static boolean usePreffix4Feature;
    public static boolean useSuffix1Feature;
    public static boolean useSuffix2Feature;
    public static boolean useSuffix3Feature;
    public static boolean useSuffix4Feature;
    public static boolean useFirstNameFeature;
    public static boolean useSimilarWord1Feature;
    public static boolean useSimilarWord2Feature;
    public static boolean useSimilarWord3Feature;
    public static boolean useSimilarWord4Feature;
    public static boolean useCamelCaseFeature;
    public static boolean useDBPediaPersonLastNameFeature;
    public static boolean useDBPediaPersonListFeature;
    public static boolean useDBPediaLocationListFeature;
    public static boolean useTopicClass100Feature;
    public static boolean useTopicClass50Feature;
    public static boolean useTopicClass200Feature;
    public static boolean useTopicClass500Feature;
    public static boolean useTopicClassUpper100Feature;
    public static boolean useCharacterCategoryFeature;   
    
}
