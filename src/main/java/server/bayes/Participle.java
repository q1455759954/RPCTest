package server.bayes;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class Participle {


    public String[] participle(String keyWord) {
        String text=keyWord;
        //创建分词对象
        Analyzer anal=new IKAnalyzer(true);
        StringReader reader=new StringReader(text);
        //分词
        TokenStream ts= null;
        try {
            ts = anal.tokenStream("", reader);
            ts.reset();

            CharTermAttribute term=ts.getAttribute(CharTermAttribute.class);
            List<String> list = new ArrayList<>();
            //遍历分词数据
            while(ts.incrementToken()){
                list.add(term.toString());
            }
            reader.close();
            String[] result = new String[list.size()];
            int i=0;
            for (String a: list){
                result[i]=a;
                i++;
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) throws Exception {
//        Participle.participle(null,null);
    }

}
