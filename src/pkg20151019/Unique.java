/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg20151019;

import fastx.Fasta;
import fastx.Fastx;
import java.beans.XMLEncoder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author shengjia
 */
public class Unique {

    static final int length = 23;
    static String targetchr = "";
    static Set<String> filter;
    static List<Fasta> fas;

    public static void main(String[] args) throws Exception {
        String in = args[1];
        targetchr = args[2];
        int timethred = 1;
        if (args.length > 3) {
            timethred = Integer.parseInt(args[3]);
        }

        String spliter = ";";
        BufferedReader inbr = new BufferedReader(new FileReader(in));
        System.err.println("loading fa @"+System.currentTimeMillis());
        fas = Fasta.fasta2list(inbr);
        System.err.println("loaded fa @"+System.currentTimeMillis());
        Fasta targetfa = null;
        filter = new HashSet<String>(800000000);
        System.err.println("hashset inited @"+System.currentTimeMillis());
        //make filter
        ExecutorService service = Executors.newFixedThreadPool(15);
        List<Future<HashSet<String>>> fus = new ArrayList<Future<HashSet<String>>>();
        List<String> ids = new ArrayList<String>();
        for (int fai = 0; fai < fas.size(); fai++) {
            Fasta fa = fas.get(fai);
            if (fa.getName().contentEquals(targetchr)) {
                targetfa = fa;
                continue;
            } else {
                Callable<HashSet<String>> call = new chr(fa);
                fus.add(service.submit(call));
                ids.add(fa.getName());
            }

        }
        for (int i = 0; i < fus.size(); i++) {
            Future<HashSet<String>> f = fus.get(i);
            HashSet<String> result = f.get();
            System.err.println("hashset: adding " + filter.size() + " + " + result.size() + " @" + System.currentTimeMillis());
            filter.addAll(result);
            System.err.println("hashset: " + ids.get(i) + " added, size: " + filter.size() + " @" + System.currentTimeMillis());
        }
        service.shutdown();
        //

        //filter
        msg("finally processing " + targetfa.getName());
        HashMap<String, String> ds = new HashMap<String, String>();
        String seq = targetfa.getSeq().toUpperCase();
        for (int i = 0; i < (seq.length() - length + 1); i++) {
            if ((seq.charAt(i) == 'C' && seq.charAt(i + 1) == 'C')
                    || (seq.charAt(i + length - 1) == 'G' && seq.charAt(i + length - 2) == 'G')) {
                String substr = seq.substring(i, i + length).toUpperCase();
                if (!filter.contains(substr)) {
                    if (ds.containsKey(substr)) {
                        String value = ds.get(substr) + spliter + (i + 1);
                        ds.put(substr, value);
                    } else {
                        ds.put(substr, "" + (i + 1));
                    }
                }
            }
        }
        //
        msg("outputing");
        Set<String> keys = ds.keySet();
        for (String key : keys) {
            int times = ds.get(key).split(spliter).length;
            if (times >= timethred) {
                System.out.println(key + "\t" + times + "\t" + ds.get(key));
            }
        }
    }

    synchronized static void msg(String str) {
        System.err.println(str);
    }

}

class chr implements Callable<HashSet<String>> {

    synchronized static void msg(String str) {
        System.err.println(str);
    }

    public chr(Fasta myfa) {
        super();
        fa = myfa;
    }
    Fasta fa;

    @Override
    public HashSet<String> call() {
        msg("processing " + fa.getName());
        String seq = fa.getSeq().toUpperCase();
        HashSet<String> result = new HashSet<String>();
        for (int i = 0; i < (seq.length() - Unique.length + 1); i++) {
            if ((seq.charAt(i) == 'C' && seq.charAt(i + 1) == 'C')
                    || (seq.charAt(i + Unique.length - 1) == 'G' && seq.charAt(i + Unique.length - 2) == 'G')) {
                String substr = seq.substring(i, i + Unique.length).toUpperCase();
                result.add(substr);
                result.add(Fastx.reverseCompliment(substr));
            }
        }
        msg("processed " + fa.getName());
        try {
            File f = new File("xml", fa.getName() + ".xml");
            if (f.exists() && f.isFile()) {
                
            }else{
                XMLEncoder x = new XMLEncoder(new FileOutputStream(f));
            x.writeObject(result);
            x.close();
            msg("outputed"+fa.getName());
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(chr.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }
}
