/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg20151019;

import fastx.Fasta;
import fastx.Fastx;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author shengjia
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        if (args[0].contentEquals("redup")) {
            Main1023.main(args);
            return;
        }else if(args[0].contentEquals("unique")){
            Unique.main(args);
            return;
        }
        int length = 23;
        String spliter = ";";
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        List<Fasta> fas = fastx.Fasta.fasta2list(br);
        HashMap<String, String> ds = new HashMap<String, String>();
        HashMap<String, String> revds = new HashMap<String, String>();
        for (Fasta fa : fas) {
//            if (!fa.getName().equalsIgnoreCase("chry")) {
//                continue;
//            }
            String seq = fa.getSeq();
            for (int i = 0; i < (seq.length() - length + 1); i++) {
                String substr = seq.substring(i, i + length).toUpperCase();
                if (substr.startsWith("CC") || substr.endsWith("GG")) {
                    if (ds.containsKey(substr)) {
                        String value = ds.get(substr) + spliter + (i + 1);
                        ds.put(substr, value);
                    } else {
                        ds.put(substr, "" + (i + 1));
                    }
                }
            }
        }
        System.err.println("chrY processed in");

        //redup
//        for (Fasta fa : fas) {
//            if (fa.getName().equalsIgnoreCase("chry") || fa.getName().length()>5) {
//                continue;
//            }
//            String seq = fa.getSeq();
//            for (int pos = 0; pos < seq.length() - length + 1; pos++) {
//                String str = seq.substring(pos, pos + length).toUpperCase();
//                if (str.startsWith("CC") || str.endsWith("GG")) {
//                    ds.remove(str);
//                }
//            }
//            System.err.println(fa.getName()+" processed out");
//        }

        PrintWriter pw = new PrintWriter(args[0] + ".out");
        for (String key : ds.keySet()) {
            int times = ds.get(key).split(spliter).length;
            if (times >= 3) {
                pw.println(key + "\t" + times + "\t" + ds.get(key));
            }
        }
        pw.close();


    }
}
