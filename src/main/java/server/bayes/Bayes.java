package server.bayes;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 训练器
 *
 * @author duyf
 *
 */
public class Bayes implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public final static String SERIALIZABLE_PATH = "C:\\logs\\test\\Train.ser";
    // 训练集的位置
    private String trainPath = "C:\\logs\\test";

    // 类别序号对应的实际名称
    private Map<String, String> classMap = new HashMap<String, String>();

    // 类别对应的txt文本数
    private Map<String, Integer> classP = new ConcurrentHashMap<String, Integer>();

    // 所有文本数
    private AtomicInteger actCount = new AtomicInteger(0);



    // 每个类别对应的词典和频数
    private Map<String, Map<String, Double>> classWordMap = new ConcurrentHashMap<String, Map<String, Double>>();

    // 分词器
    private transient Participle participle;

    private static Bayes trainInstance = new Bayes();

    public static Bayes getInstance() {
        trainInstance = new Bayes();

        // 读取序列化在硬盘的本类对象
        FileInputStream fis;
        try {
            File f = new File(SERIALIZABLE_PATH);
            if (f.length() != 0) {
                fis = new FileInputStream(SERIALIZABLE_PATH);
                ObjectInputStream oos = new ObjectInputStream(fis);
                trainInstance = (Bayes) oos.readObject();
                trainInstance.participle = new Participle();
            } else {
                trainInstance = new Bayes();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return trainInstance;
    }

    private Bayes() {
        this.participle = new Participle();
    }

    public String readtxt(String path) {
        BufferedReader br = null;
        StringBuilder str = null;
        try {
            br = new BufferedReader(new FileReader(path));

            str = new StringBuilder();

            String r = br.readLine();

            while (r != null) {
                str.append(r);
                r = br.readLine();

            }

            return str.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            str = null;
            br = null;
        }

        return "";
    }

    /**
     * 训练数据
     */
    public void realTrain() {
        // 初始化
        classMap = new HashMap<String, String>();
        classP = new HashMap<String, Integer>();
        actCount.set(0);
        classWordMap = new HashMap<String, Map<String, Double>>();

        // classMap.put("C000007", "汽车");
        classMap.put("001.txt", "游戏");
        classMap.put("002.txt", "搞笑");
        classMap.put("003.txt", "生活");

        // 计算各个类别的样本数
        Set<String> keySet = classMap.keySet();

        // 所有词汇的集合,是为了计算每个单词在多少篇文章中出现，用于后面计算df
        final Set<String> allWords = new HashSet<String>();

        // 存放每个类别的文件词汇内容
        final Map<String, List<String[]>> classContentMap = new ConcurrentHashMap<String, List<String[]>>();

        for (String classKey : keySet) {

            Participle participle = new Participle();
            Map<String, Double> wordMap = new HashMap<String, Double>();
            File f = new File(trainPath + File.separator + classKey);
//            File[] files = f.listFiles(new FileFilter() {
//
//                @Override
//                public boolean accept(File pathname) {
//                    if (pathname.getName().endsWith(".txt")) {
//                        return true;
//                    }
//                    return false;
//                }
//
//            });
            File[] files = new File[1];
            files[0] = f;

            // 存储每个类别的文件词汇向量
            List<String[]> fileContent = new ArrayList<String[]>();
            if (files != null) {
                for (File txt : files) {
                    String content = readtxt(txt.getAbsolutePath());
                    // 分词
                    String[] word_arr = participle.participle(content);
                    fileContent.add(word_arr);
                    // 统计每个词出现的个数
                    for (String word : word_arr) {
                        if (wordMap.containsKey(word)) {
                            Double wordCount = wordMap.get(word);
                            wordMap.put(word, wordCount + 1);
                        } else {
                            wordMap.put(word, 1.0);
                        }

                    }
                }
            }

            // 每个类别对应的词典和频数
            classWordMap.put(classKey, wordMap);

            // 每个类别的文章数目
            classP.put(classKey, files.length);
            actCount.addAndGet(files.length);
            classContentMap.put(classKey, fileContent);

        }





        // 把训练好的训练器对象序列化到本地 （空间换时间）
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(SERIALIZABLE_PATH);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 分类
     *
     * @param text
     * @return 返回各个类别的概率大小
     */
    public String classify(String text) {
        // 分词，并且去重
        String[] text_words = participle.participle(text);

        Map<String, Double> frequencyOfType = new HashMap<String, Double>();
        Set<String> keySet = classMap.keySet();
        for (String classKey : keySet) {
            double typeOfThis = 1.0;
            Map<String, Double> wordMap = classWordMap.get(classKey);
            for (String word : text_words) {
                Double wordCount = wordMap.get(word);
                int articleCount = classP.get(classKey);

                /*
                 * Double wordidf = idfMap.get(word); if(wordidf==null){
                 * wordidf=0.001; }else{ wordidf = Math.log(actCount / wordidf); }
                 */

                // 假如这个词在类别下的所有文章中木有，那么给定个极小的值 不影响计算
                double term_frequency = (wordCount == null) ? ((double) 1 / (articleCount + 1))
                        : (wordCount / articleCount);

                // 文本在类别的概率 在这里按照特征向量独立统计，即概率=词汇1/文章数 * 词汇2/文章数 。。。
                // 当double无限小的时候会归为0，为了避免 *10

                typeOfThis = typeOfThis * term_frequency * 10;
                typeOfThis = ((typeOfThis == 0.0) ? Double.MIN_VALUE
                        : typeOfThis);
                // System.out.println(typeOfThis+" : "+term_frequency+" :
                // "+actCount);
            }

            typeOfThis = ((typeOfThis == 1.0) ? 0.0 : typeOfThis);

            // 此类别文章出现的概率
            double classOfAll = classP.get(classKey) / actCount.doubleValue();

            // 根据贝叶斯公式 $(A|B)=S(B|A)*S(A)/S(B),由于$(B)是常数，在这里不做计算,不影响分类结果
            frequencyOfType.put(classKey, typeOfThis * classOfAll);
        }
        double max = 0;
        String maxR = "";
        for (String key : frequencyOfType.keySet()) {
            if (max<frequencyOfType.get(key)){
                max=frequencyOfType.get(key);
                maxR = key.equals("001.txt")?"game":(key.equals("002.txt")?"fun":"life");
            }
        }

        return maxR;
    }

    public void pringAll() {
        Set<Map.Entry<String, Map<String, Double>>> classWordEntry = classWordMap
                .entrySet();
        for (Map.Entry<String, Map<String, Double>> ent : classWordEntry) {
            System.out.println("类别： " + ent.getKey());
            Map<String, Double> wordMap = ent.getValue();
            Set<Map.Entry<String, Double>> wordMapSet = wordMap.entrySet();
            for (Map.Entry<String, Double> wordEnt : wordMapSet) {
                System.out.println(wordEnt.getKey() + ":" + wordEnt.getValue());
            }
        }
    }

    public Map<String, String> getClassMap() {
        return classMap;
    }

    public void setClassMap(Map<String, String> classMap) {
        this.classMap = classMap;
    }

    public static void main(String[] args){
//        Bayes bayes = new Bayes();
//        bayes.realTrain();
        Bayes bayes = Bayes.getInstance();
        System.out.println(bayes.classMap.size());
//        Map<String, Double> result = bayes.classify("这个笑话太好笑了");
//        double max = 0;
//        String re = "";
//        for (String key : result.keySet()) {
//            if (max<result.get(key)){
//                max=result.get(key);
//                re = key;
//            }
//            System.out.println("Key = " + key + "   value = " + result.get(key));
//        }
//        System.out.println("最后结果为："+re);
    }
}
